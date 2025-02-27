package com.future.accountservice.payload;

import com.future.accountservice.entity.Address;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Long id;
    private Long userId;
    private Address.AddressType addressType;
    private String recipientName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
}
