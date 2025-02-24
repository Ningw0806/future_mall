package com.future.paymentservice.repository;

import com.future.paymentservice.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, Long> {
}
