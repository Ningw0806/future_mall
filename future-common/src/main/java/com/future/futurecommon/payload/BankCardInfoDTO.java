package com.future.futurecommon.payload;

import com.future.futurecommon.constant.BankCardType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCardInfoDTO {

    private String userFirstName;

    private String userLastName;

    private String cardNumber;

    private String nameOnCard;

    private String securityCode;

    private BankCardType bankCardType;

}
