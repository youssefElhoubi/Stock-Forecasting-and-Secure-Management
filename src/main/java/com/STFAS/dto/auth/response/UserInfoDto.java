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
public class UserInfoDto {

    private String id;
    private String name;
    private String email;
    private Role role;
}