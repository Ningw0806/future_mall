package com.future.accountservice.payload;

import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean active;
    private Set<String> roles;
    private Set<AddressDTO> addresses;
    private Set<PaymentMethodDTO> paymentMethods;
}

