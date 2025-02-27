package com.future.accountservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String phone;
}
