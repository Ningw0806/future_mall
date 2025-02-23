package com.future.orderservice.service.impl;

import com.future.futurecommon.constant.OrderEventType;
import com.future.futurecommon.constant.OrderStatus;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import com.future.orderservice.entity.*;
import com.future.orderservice.exception.OrderAPIException;
import com.future.orderservice.exception.ResourceNotFoundException;
import com.future.orderservice.payload.*;
import com.future.orderservice.repository.*;
import com.future.orderservice.service.OrderService;
import com.future.orderservice.specification.OrderSpecification;
import com.future.orderservice.util.OrderEventUtil;
import com.future.orderservice.util.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Value("${kafka.producer-config.topic}")
    private String KAFKA_TOPIC;

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderEventRepository orderEventRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderCancellationRepository orderCancellationRepository;
    private final OrderMapper orderMapper;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final KafkaTemplate<String, OrderInfoDTO> orderKafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, OrderAddressRepository orderAddressRepository, OrderEventRepository orderEventRepository, OrderItemRepository orderItemRepository, OrderCancellationRepository orderCancellationRepository, OrderMapper orderMapper, SnowflakeIdGenerator snowflakeIdGenerator, KafkaTemplate<String, OrderInfoDTO> orderKafkaTemplate) {
        this.orderRepository = orderRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.orderEventRepository = orderEventRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderCancellationRepository = orderCancellationRepository;
        this.orderMapper = orderMapper;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
        this.orderKafkaTemplate = orderKafkaTemplate;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // Generate Order ID & OrderAddress ID
        long orderId = snowflakeIdGenerator.generateId();
        long orderAddressId = snowflakeIdGenerator.generateId();
        long orderEventId = snowflakeIdGenerator.generateId();

        // Build Order + Order Items
        OrderDTO orderDTO = orderRequestDTO.getOrder();
        List<OrderItemDTO> orderItemDTOList = orderDTO.getOrderItemList();

        Order order = orderMapper.toOrderEntity(orderDTO);
        order.setId(orderId);
        order.setAddressId(orderAddressId);

        Order finalOrder = order;
        List<OrderItem> orderItems = orderItemDTOList.stream()
                .filter(Objects::nonNull)
                .map(orderMapper::toOrderItemEntity)
                .peek(orderItem -> orderItem.setOrder(finalOrder))
                .toList();

        order.setOrderItems(orderItems);

        // Build Order Address
        OrderAddress address = orderMapper.toOrderAddressEntity(orderRequestDTO.getAddress());
        address.setId(orderAddressId);

        // wait check product quantity

        // 1. save order
        order = orderRepository.save(order);

        // 2. save address
        address.setOrder(order);
        address = orderAddressRepository.save(address);

        // 3. save order event
        Map<String, Object> eventDataMap = Map.of(
                "userId", order.getUserId(),
                "orderId", orderId,
                "Status Message", "Customer Create Order"
        );
        OrderEvent orderEvent = OrderEventUtil.generateOrderEvent(order, eventDataMap, OrderStatus.CREATED, OrderStatus.CREATED, OrderEventType.ORDER_CREATED);
        orderEvent.setId(orderEventId);
        orderEventRepository.save(orderEvent);

        // 4. set return
        return generateOrderResponseDTO(order, address, order.getOrderItems(), orderId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public OrderResponseDTO updateOrder(long orderId, OrderRequestDTO orderRequestDTO) {
        // check whether order exist
        Order oriOrder = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        OrderAddress oriOrderAddress = orderAddressRepository.findByOrderId(orderId);
        OrderEvent prevOrderEvent = orderEventRepository.findFirstByOrderIdOrderByCreateAtDesc(orderId);

        // Map existing items by productId for quick lookup
        Map<Long, OrderItem> existingItemsMap = oriOrder.getOrderItems()
                .stream()
                .collect(Collectors.toMap(OrderItem::getProductId, item -> item));

        // Process updated items
        OrderDTO orderDTO = orderRequestDTO.getOrder();
        List<OrderItemDTO> updatedOrderItemDTOList = orderDTO.getOrderItemList();

        // wait check product quantity

        for (OrderItemDTO updatedItemDTO : updatedOrderItemDTOList) {
            OrderItem existingItem = existingItemsMap.get(updatedItemDTO.getProductId());

            if (existingItem != null) {
                // Update existing item
                existingItem.setQuantity(updatedItemDTO.getQuantity());
                existingItem.setUnitPrice(updatedItemDTO.getUnitPrice());
                existingItem.setTotalPrice(updatedItemDTO.getUnitPrice().multiply(
                        java.math.BigDecimal.valueOf(updatedItemDTO.getQuantity())
                ));
                existingItemsMap.remove(updatedItemDTO.getProductId()); // Mark as processed
            } else {
                // Add new item
                OrderItem newItem = OrderItem.builder()
                        .order(oriOrder)
                        .productId(updatedItemDTO.getProductId())
                        .quantity(updatedItemDTO.getQuantity())
                        .unitPrice(updatedItemDTO.getUnitPrice())
                        .totalPrice(updatedItemDTO.getUnitPrice().multiply(
                                java.math.BigDecimal.valueOf(updatedItemDTO.getQuantity())
                        ))
                        .build();
                oriOrder.getOrderItems().add(newItem);
            }
        }

        // Remove items that are no longer part of the order
        for (OrderItem itemToRemove : existingItemsMap.values()) {
            oriOrder.getOrderItems().remove(itemToRemove);
            orderItemRepository.delete(itemToRemove);
        }

        // Update order total price
        oriOrder.setTotalPrice(
                oriOrder.getOrderItems().stream()
                        .map(OrderItem::getTotalPrice)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
        );

        // Update address
        OrderAddress updatedAddress = orderMapper.toOrderAddressEntity(orderRequestDTO.getAddress());
        updatedAddress.setOrder(oriOrder);

        // 1. save order
        oriOrder = orderRepository.save(oriOrder);

        // 2. save address
        updatedAddress = orderAddressRepository.save(updatedAddress);

        // 3. save order event
        Map<String, Object> eventDataMap = Map.of(
                "userId", oriOrder.getUserId(),
                "orderId", orderId,
                "Status Message", "Customer Update Order"
        );
        OrderEvent orderEvent = OrderEventUtil.generateOrderEvent(oriOrder, eventDataMap, prevOrderEvent.getNewStatus(), prevOrderEvent.getNewStatus(), OrderEventType.ORDER_UPDATED);
        orderEvent.setId(snowflakeIdGenerator.generateId());
        orderEventRepository.save(orderEvent);

        return generateOrderResponseDTO(oriOrder, updatedAddress, oriOrder.getOrderItems(), orderId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public OrderDTO cancelOrder(long orderId, OrderCancellationDTO orderCancellationDTO) {
        // check whether order exist
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        OrderEvent prevOrderEvent = orderEventRepository.findFirstByOrderIdOrderByCreateAtDesc(orderId);

        if (order.getStatus().equals(OrderStatus.SHIPPED) || order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new OrderAPIException("Can't cancel shipped order",
                    HttpStatus.BAD_REQUEST,
                    String.format("User [%d] can't cancel to shipped Order [%d]", order.getUserId(), order.getId()));
        }

        // 1. save order
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        // 2. save to cancellation repo
        OrderCancellation orderCancellation = orderMapper.toOrderCancellationEntity(orderCancellationDTO);
        orderCancellation.setOrder(order);
        orderCancellationRepository.save(orderCancellation);

        // 3. record order event
        Map<String, Object> eventDataMap = Map.of(
                "userId", order.getUserId(),
                "orderId", orderId,
                "Status Message", "Customer Cancel Order"
        );
        OrderEvent orderEvent = OrderEventUtil.generateOrderEvent(order, eventDataMap, prevOrderEvent.getNewStatus(), OrderStatus.CANCELLED, OrderEventType.ORDER_CANCELLED);
        orderEvent.setId(snowflakeIdGenerator.generateId());
        orderEventRepository.save(orderEvent);

        return orderMapper.toOrderDTO(order);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public OrderDTO confirmOrder(Long orderId, Long userId, BankCardInfoDTO bankCardInfoDTO) {
        // check order
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (!OrderStatus.CREATED.equals(order.getStatus())) {
            throw new OrderAPIException("Can't confirm order",
                    HttpStatus.BAD_REQUEST,
                    String.format("User [%d] can't confirm Order [%d] - Reason: Order Status is [%s]",
                            order.getUserId(), order.getId(), order.getStatus()));
        }
        if (userId.compareTo(order.getUserId()) != 0) {
            throw new OrderAPIException("User ID mismatch",
                    HttpStatus.CONFLICT,
                    String.format("User [%d] does not have access to Order [%d]", userId, order.getId()));
        }

        OrderAddress orderAddress = orderAddressRepository.findByOrderId(orderId);
        if (orderAddress == null) {
            throw new OrderAPIException("Can't confirm order",
                    HttpStatus.BAD_REQUEST,
                    String.format("User [%d] can't confirm Order [%d] - Reason: Order Address Not Provided", order.getUserId(), order.getId()));
        }

        OrderEvent prevOrderEvent = orderEventRepository.findFirstByOrderIdOrderByCreateAtDesc(orderId);

        // check quantity

        // update order status
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        // record event
        Map<String, Object> eventDataMap = Map.of(
                "userId", order.getUserId(),
                "orderId", orderId,
                "Status Message", "Customer Confirmed Order and Send to Payment Service By Kafka"
        );
        OrderEvent orderEvent = OrderEventUtil.generateOrderEvent(order, eventDataMap, prevOrderEvent.getNewStatus(), OrderStatus.CONFIRMED, OrderEventType.ORDER_CONFIRMED);
        orderEvent.setId(snowflakeIdGenerator.generateId());
        orderEventRepository.save(orderEvent);

        // send to payment service
        try {
            OrderInfoDTO orderInfoDTO = generateOrderInfoDTO(order, bankCardInfoDTO);
            orderKafkaTemplate.send(KAFKA_TOPIC, orderId.toString(), orderInfoDTO);
            logger.info("Order [{}] successfully send to Kafka", orderId);
        } catch (Exception ex) {
            throw new OrderAPIException("Kafka failure",
                    HttpStatus.SERVICE_UNAVAILABLE,
                    String.format("Order [%d] could not be sent to payment service, please try again later.", orderId));
        }

        return orderMapper.toOrderDTO(order);
    }

    @Override
    public OrderResponseDTO getOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (userId.compareTo(order.getUserId()) != 0) {
            throw new OrderAPIException("User ID mismatch",
                    HttpStatus.CONFLICT,
                    String.format("User [%d] does not have access to Order [%d]", userId, order.getId()));
        }

        OrderAddress orderAddress = orderAddressRepository.findByOrderId(orderId);

        OrderResponseDTO orderResponseDTO = generateOrderResponseDTO(order, orderAddress, order.getOrderItems(), orderId);

        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            OrderCancellation orderCancellation = orderCancellationRepository.findByOrderId(orderId);
            if (orderCancellation != null) {
                OrderCancellationDTO orderCancellationDTO = orderMapper.toOrderCancellationDTO(orderCancellation);
                orderCancellationDTO.setOrderId(orderId);
                orderResponseDTO.setCancel(orderCancellationDTO);
            }
        }

        return orderResponseDTO;
    }

    @Override
    public Page<OrderDTO> getFilteredOrders(OrderQueryDTO orderQueryDTO, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        return orderRepository.findAll(
                OrderSpecification.filterOrders(orderQueryDTO.getUserId(),
                        orderQueryDTO.getStartDate(),
                        orderQueryDTO.getEndDate(),
                        orderQueryDTO.getOrderStatus(),
                        orderQueryDTO.getPaymentStatus()),
                pageable
        ).map(orderMapper::toOrderDTO);
    }

    @Override
    public Page<OrderDTO> getOrderByUserId(Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toOrderDTO);
    }

    private OrderResponseDTO generateOrderResponseDTO(Order order, OrderAddress address, List<OrderItem> orderItems, Long orderId) {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        // Order
        OrderDTO returnOrderDTO = orderMapper.toOrderDTO(order);
        returnOrderDTO.setOrderItemList(orderItems.stream()
                        .map(orderMapper::toOrderItemDTO)
                        .toList());
        orderResponseDTO.setOrder(returnOrderDTO);
        // Address
        OrderAddressDTO returnOrderAddressDTO = orderMapper.toOrderAddressDTO(address);
        returnOrderAddressDTO.setOrderId(orderId);
        orderResponseDTO.setAddress(returnOrderAddressDTO);

        return orderResponseDTO;
    }

    private OrderInfoDTO generateOrderInfoDTO(Order order, BankCardInfoDTO bankCardInfoDTO) {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderId(order.getId());
        orderInfoDTO.setUserId(order.getUserId());
        orderInfoDTO.setTotalAmount(order.getTotalPrice());
        orderInfoDTO.setOrderStatus(order.getStatus());
        orderInfoDTO.setPaymentStatus(order.getPaymentStatus());
        orderInfoDTO.setBankCardInfo(bankCardInfoDTO);

        return orderInfoDTO;
    }
}
