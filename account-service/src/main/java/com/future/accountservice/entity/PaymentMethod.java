package com.future.accountservice.entity;

import com.future.futurecommon.constant.BankCardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {
    @Id
    private Long id; // Snowflake ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "name_on_card", nullable = false)
    private String nameOnCard;

    @Column(name = "expiration_month", nullable = false)
    private Integer expirationMonth;

    @Column(name = "expiration_year", nullable = false)
    private Integer expirationYear;

    @Column(name = "security_code", nullable = false)
    private String securityCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private BankCardType cardType;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Column(name = "billing_address_id")
    private Long billingAddressId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}