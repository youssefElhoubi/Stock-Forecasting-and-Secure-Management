package com.STFAS.mapper;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import org.mapstruct.Mapper;
import com.STFAS.entity.User;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // This makes it a Spring Bean (@Component)
public interface UserMapper {

    // 1. Convert User Entity to UserInfo DTO
    UserInfoDto toUserInfoDto(User user);

    // 2. Convert Registration DTO to User Entity
    // We ignore ID because MongoDB generates it
    // We ignore Role if you want to set it manually in the service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target="warehouse" , ignore = true)
    User toEntity(SignUpRequestDto registerRequestDto);

    // 3. Convert User to AuthResponse (Token is handled separately usually)
    @Mapping(target = "token", ignore = true)
    AuthResponseDto toAuthResponseDto(User user);
}