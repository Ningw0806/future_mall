package com.future.paymentservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.futurecommon.constant.PaymentEventType;
import com.future.futurecommon.constant.PaymentStatus;
import com.future.paymentservice.entity.Payment;
import com.future.paymentservice.entity.PaymentEvent;

import java.util.Map;

public class PaymentEventUtil {

    public static PaymentEvent generateOrderEvent(Payment payment, Map<String, Object> eventDataMap, PaymentStatus preStatus, PaymentStatus newStatus, PaymentEventType paymentEventType) {
        String eventData = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            eventData = objectMapper.writeValueAsString(eventDataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON for eventData", e);
        }

        return PaymentEvent.builder()
                .payment(payment)
                .eventType(paymentEventType)
                .previousStatus(preStatus)
                .newStatus(newStatus)
                .eventData(eventData)
                .build();
    }

}
