package com.future.orderservice.repository;

import com.future.orderservice.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {

    OrderAddress findByOrderId(long orderId);
}
