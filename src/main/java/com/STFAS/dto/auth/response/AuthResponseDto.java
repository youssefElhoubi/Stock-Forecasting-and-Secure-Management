package com.STFAS.dto.auth.response;

import com.STFAS.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String accessToken; // The JWT Token
    private String id;
    private Role role;
    private String token;
}