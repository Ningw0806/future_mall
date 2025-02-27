package com.future.accountservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;
}
