package com.future.paymentservice.service;

import com.future.paymentservice.payload.PaymentDTO;

public interface PaymentService {

    PaymentDTO findPaymentById(Long paymentId);

}
