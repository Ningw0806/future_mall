package com.future.paymentservice.service;

import com.future.futurecommon.util.SnowflakeIdGenerator;
import com.future.paymentservice.entity.PaymentEvent;
import com.future.paymentservice.repository.PaymentEventRepository;
import com.future.paymentservice.repository.PaymentRefundRepository;
import com.future.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.consumer-config.topic}")
    private String listenerTopic;

    private final PaymentRepository paymentRepository;
    private final PaymentRefundRepository paymentRefundRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public PaymentRequestListener(PaymentRepository paymentRepository, PaymentRefundRepository paymentRefundRepository, PaymentEventRepository paymentEventRepository, SnowflakeIdGenerator snowflakeIdGenerator) {
        this.paymentRepository = paymentRepository;
        this.paymentRefundRepository = paymentRefundRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    @KafkaListener(topics = "")
    @Transactional
    public void handlePaymentRequest() {

    }
}
