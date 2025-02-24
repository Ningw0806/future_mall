package com.future.paymentservice.repository;

import com.future.futurecommon.constant.PaymentStatus;
import com.future.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findPaymentByOrderId(Long orderId);

    List<Payment> findPaymentsByPaymentStatus(PaymentStatus paymentStatus);
}
