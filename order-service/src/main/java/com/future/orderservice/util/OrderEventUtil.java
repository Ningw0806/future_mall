package com.future.orderservice.util;

import com.future.futurecommon.constant.OrderEventType;
import com.future.futurecommon.constant.OrderStatus;
import com.future.orderservice.entity.Order;
import com.future.orderservice.entity.OrderEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class OrderEventUtil {

    public static OrderEvent generateOrderEvent(Order order, Map<String, Object> eventDataMap, OrderStatus preStatus, OrderStatus newStatus, OrderEventType orderEventType) {
        String eventData = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            eventData = objectMapper.writeValueAsString(eventDataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON for eventData", e);
        }

        return OrderEvent.builder()
                .order(order)
                .orderEventType(orderEventType)
                .previousStatus(preStatus)
                .newStatus(newStatus)
                .eventData(eventData)
                .build();
    }
}
