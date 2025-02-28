package com.future.accountservice.payload;

import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private Set<String> roles;
}
