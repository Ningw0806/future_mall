package com.future.accountservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
}
