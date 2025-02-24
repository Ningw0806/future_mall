package com.future.paymentservice.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentGateway {

    // 模拟返款 -- 80% success rate
    public boolean processUserPayment(Long orderId, BigDecimal amount) {
        return Math.random() < 0.8;
    }
}
