package com.future.paymentservice.service;

import com.future.futurecommon.constant.PaymentEventType;
import com.future.futurecommon.constant.PaymentStatus;
import com.future.futurecommon.payload.OrderInfoDTO;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import com.future.paymentservice.entity.Payment;
import com.future.paymentservice.entity.PaymentEvent;
import com.future.paymentservice.exception.PaymentAPIException;
import com.future.paymentservice.repository.PaymentEventRepository;
import com.future.paymentservice.repository.PaymentRepository;
import com.future.paymentservice.util.PaymentEventUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentRequestListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.consumer-config.topic}")
    private String listenerTopic;

    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public PaymentRequestListener(PaymentRepository paymentRepository, PaymentEventRepository paymentEventRepository, SnowflakeIdGenerator snowflakeIdGenerator) {
        this.paymentRepository = paymentRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    @KafkaListener(topics = "user_payment_requests")
    @Transactional
    public void handlePaymentRequest(OrderInfoDTO orderInfoDTO, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment) {
        try {
            Payment payment = this.paymentRepository.findPaymentByOrderId(orderInfoDTO.getOrderId());
            if (payment != null) {
                throw new PaymentAPIException("Payment already exists",
                        HttpStatus.BAD_REQUEST,
                        String.format("Payment already exists: Order [%d], Existing Payment [%d]", orderInfoDTO.getOrderId(), payment.getId()));
            }

            // Payment 处理
            payment = new Payment();
            payment.setId(snowflakeIdGenerator.generateId());
            payment.setOrderId(orderInfoDTO.getOrderId());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setUserFirstName(orderInfoDTO.getBankCardInfo().getUserFirstName());
            payment.setUserLastName(orderInfoDTO.getBankCardInfo().getUserLastName());
            payment.setCardNumber(orderInfoDTO.getBankCardInfo().getCardNumber());
            payment.setNameOnCard(orderInfoDTO.getBankCardInfo().getNameOnCard());
            payment.setSecurityCode(orderInfoDTO.getBankCardInfo().getSecurityCode());
            payment.setBankCardType(orderInfoDTO.getBankCardInfo().getBankCardType());
            payment.setAmount(orderInfoDTO.getTotalAmount());
            paymentRepository.save(payment);

            // Payment Event 处理
            Map<String, Object> eventDataMap = Map.of(
                    "userId", orderInfoDTO.getUserId(),
                    "orderId", orderInfoDTO.getOrderId(),
                    "Status Message", "Record Payment Request from Order Service"
            );
            PaymentEvent paymentEvent = PaymentEventUtil.generateOrderEvent(payment, eventDataMap, PaymentStatus.PENDING, PaymentStatus.PENDING, PaymentEventType.PAYMENT_INITIATED);
            paymentEvent.setId(snowflakeIdGenerator.generateId());
            paymentEventRepository.save(paymentEvent);

            logger.info("Payment event created: {}", orderInfoDTO.getOrderId());
        } catch (Exception ex) {
            logger.error("Failed to process payment request: {}", orderInfoDTO.getOrderId(), ex);
            // 未来功能，失败处理逻辑
        } finally {
            // 手动提交偏移量
            acknowledgment.acknowledge();
        }
    }
}
