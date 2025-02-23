package com.future.orderservice.specification;

import com.future.futurecommon.constant.OrderStatus;
import com.future.futurecommon.constant.PaymentStatus;
import com.future.orderservice.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> filterOrders(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                                    OrderStatus orderStatus, PaymentStatus paymentStatus) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            } else if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            if (orderStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), orderStatus));
            }
            if (paymentStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
            }

            query.orderBy(criteriaBuilder.desc(root.get("createdAt"))); // Sort by newest orders first
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
