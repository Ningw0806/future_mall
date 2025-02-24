package com.future.paymentservice.repository;

import com.future.paymentservice.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    PaymentEvent findLastPaymentEventByOrderId(Long orderId);
}
