package com.future.orderservice.repository;

import com.future.orderservice.entity.OrderCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCancellationRepository extends JpaRepository<OrderCancellation, Long> {
    OrderCancellation findByOrderId(long orderId);
}
