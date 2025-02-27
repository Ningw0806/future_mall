package com.future.accountservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    private String usernameOrEmail;
    private String password;
}
