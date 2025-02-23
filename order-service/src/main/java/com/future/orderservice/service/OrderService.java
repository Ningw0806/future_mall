package com.future.orderservice.service;

import com.future.orderservice.payload.*;
import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    OrderResponseDTO updateOrder(long orderId, OrderRequestDTO orderRequestDTO);

    OrderDTO cancelOrder(long orderId, OrderCancellationDTO orderCancellationDTO);

    OrderDTO confirmOrder(Long orderId, Long userId);

    OrderResponseDTO getOrder(Long orderId, Long userId);

    Page<OrderDTO> getFilteredOrders(OrderQueryDTO orderQueryDTO, int page, int pageSize);

    Page<OrderDTO> getOrderByUserId(Long userId, int page, int pageSize);
}
