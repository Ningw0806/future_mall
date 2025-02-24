package com.future.paymentservice.scheduler;


import com.future.futurecommon.constant.PaymentEventType;
import com.future.futurecommon.constant.PaymentStatus;
import com.future.futurecommon.payload.PaymentResultDTO;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import com.future.paymentservice.entity.Payment;
import com.future.paymentservice.entity.PaymentEvent;
import com.future.paymentservice.exception.PaymentAPIException;
import com.future.paymentservice.repository.PaymentEventRepository;
import com.future.paymentservice.repository.PaymentRepository;
import com.future.paymentservice.service.PaymentGateway;
import com.future.paymentservice.util.PaymentEventUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PaymentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentScheduler.class);

    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentGateway paymentGateway;
    private final KafkaTemplate<String, PaymentResultDTO> paymentResultDTOKafkaTemplate;

    public PaymentScheduler(SnowflakeIdGenerator snowflakeIdGenerator, PaymentRepository paymentRepository, PaymentEventRepository paymentEventRepository, PaymentGateway paymentGateway, KafkaTemplate<String, PaymentResultDTO> paymentResultDTOKafkaTemplate) {
        this.snowflakeIdGenerator = snowflakeIdGenerator;
        this.paymentRepository = paymentRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.paymentGateway = paymentGateway;
        this.paymentResultDTOKafkaTemplate = paymentResultDTOKafkaTemplate;
    }

    @Scheduled(fixedRate = 60_000)
    @Async("paymentTaskExecutor")
    @Transactional
    public void schedulePayment() {
        List<Payment> payments = paymentRepository.findPaymentsByPaymentStatus(PaymentStatus.PENDING);

        payments.forEach(payment -> {
            try {
                PaymentEvent preEvent = paymentEventRepository.findLastPaymentEventByOrderId(payment.getOrderId());
                boolean result = paymentGateway.processUserPayment(payment.getOrderId(), payment.getAmount());
                if (result) {
                    // Save Status
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    payment.setTransactionId("Paid" + snowflakeIdGenerator.generateId());
                    paymentRepository.save(payment);

                    // Record Event
                    Map<String, Object> eventDataMap = Map.of(
                            "paymentId", payment.getId(),
                            "orderId", payment.getOrderId(),
                            "Status Message", "Payment Success and Send Back to Order Service"
                    );
                    PaymentEvent paymentEvent = PaymentEventUtil.generateOrderEvent(payment, eventDataMap, preEvent.getNewStatus(), PaymentStatus.COMPLETED, PaymentEventType.PAYMENT_SUCCESS);
                    paymentEvent.setId(snowflakeIdGenerator.generateId());
                    paymentEventRepository.save(paymentEvent);

                    // Send to kafka
                    PaymentResultDTO paymentResultDTO = new PaymentResultDTO();
                    paymentResultDTO.setOrderId(payment.getOrderId());
                    paymentResultDTO.setPaymentStatus(PaymentStatus.COMPLETED);
                    paymentResultDTO.setMessage("Payment completed");
                    paymentResultDTOKafkaTemplate.send("payment-event", paymentResultDTO);
                    logger.info("Payment Result [{}] successfully send to Kafka", paymentResultDTO.getOrderId());
                } else {
                    // 失败处理
                    int retryCount = payment.getRetryCount();
                    if (++retryCount > 3) {
                        // Save Payment
                        payment.setRetryCount(retryCount);
                        payment.setPaymentStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);

                        // Record Event
                        Map<String, Object> eventDataMap = Map.of(
                                "paymentId", payment.getId(),
                                "orderId", payment.getOrderId(),
                                "Status Message", "Payment Fail and retries exhausted"
                        );
                        PaymentEvent paymentEvent = PaymentEventUtil.generateOrderEvent(payment, eventDataMap, preEvent.getNewStatus(), PaymentStatus.FAILED, PaymentEventType.PAYMENT_FAILED);
                        paymentEvent.setId(snowflakeIdGenerator.generateId());
                        paymentEventRepository.save(paymentEvent);

                        // Send to kafka
                        PaymentResultDTO paymentResultDTO = new PaymentResultDTO();
                        paymentResultDTO.setOrderId(payment.getOrderId());
                        paymentResultDTO.setPaymentStatus(PaymentStatus.FAILED);
                        paymentResultDTO.setMessage("Payment FAILED");
                        paymentResultDTOKafkaTemplate.send("payment-event", paymentResultDTO);
                        logger.info("Payment Result [{}] successfully send to Kafka", paymentResultDTO.getOrderId());
                    } else {
                        payment.setRetryCount(retryCount);
                        paymentRepository.save(payment);

                        // Record Event
                        Map<String, Object> eventDataMap = Map.of(
                                "paymentId", payment.getId(),
                                "orderId", payment.getOrderId(),
                                "Status Message", "Payment Fail and will retry"
                        );
                        PaymentEvent paymentEvent = PaymentEventUtil.generateOrderEvent(payment, eventDataMap, preEvent.getNewStatus(), PaymentStatus.FAILED, PaymentEventType.PAYMENT_FAILED);
                        paymentEvent.setId(snowflakeIdGenerator.generateId());
                        paymentEventRepository.save(paymentEvent);
                    }
                }
            } catch (Exception e) {
                throw new PaymentAPIException("Kafka failure",
                                HttpStatus.SERVICE_UNAVAILABLE,
                                String.format("Payment Result [%d] could not be sent to order service, please try again later.", payment.getOrderId()));
            }
        });
    }

}
