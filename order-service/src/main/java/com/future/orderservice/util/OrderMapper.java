package com.future.orderservice.util;

import com.future.orderservice.entity.*;
import com.future.orderservice.payload.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final ModelMapper modelMapper;

    public OrderMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public OrderDTO toOrderDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public Order toOrderEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return modelMapper.map(orderItem, OrderItemDTO.class);
    }

    public OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO) {
        return modelMapper.map(orderItemDTO, OrderItem.class);
    }

    public OrderEventDTO toOrderEventDTO(OrderEvent orderEvent) {
        return modelMapper.map(orderEvent, OrderEventDTO.class);
    }

    public OrderEvent toOrderEventEntity(OrderEventDTO orderEventDTO) {
        return modelMapper.map(orderEventDTO, OrderEvent.class);
    }

    public OrderAddressDTO toOrderAddressDTO(OrderAddress orderAddress) {
        return modelMapper.map(orderAddress, OrderAddressDTO.class);
    }

    public OrderAddress toOrderAddressEntity(OrderAddressDTO orderAddressDTO) {
        return modelMapper.map(orderAddressDTO, OrderAddress.class);
    }

    public OrderCancellationDTO toOrderCancellationDTO(OrderCancellation orderCancellation) {
        return modelMapper.map(orderCancellation, OrderCancellationDTO.class);
    }

    public OrderCancellation toOrderCancellationEntity(OrderCancellationDTO orderCancellationDTO) {
        return modelMapper.map(orderCancellationDTO, OrderCancellation.class);
    }
}