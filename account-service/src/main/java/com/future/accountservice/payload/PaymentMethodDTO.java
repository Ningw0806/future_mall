package com.future.accountservice.payload;

import com.future.futurecommon.constant.BankCardType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodDTO {
    private Long id;
    private Long userId;
    private String cardNumber;
    private String nameOnCard;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String securityCode;
    private BankCardType cardType;
    private boolean isDefault;
    private Long billingAddressId;
}
