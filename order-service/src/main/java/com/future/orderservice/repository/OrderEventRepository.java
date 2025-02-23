package com.future.orderservice.repository;

import com.future.orderservice.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

    OrderEvent findFirstByOrderIdOrderByCreateAtDesc(long orderId);
}
