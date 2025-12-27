package com.STFAS.mapper;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import org.mapstruct.Mapper;
import com.STFAS.entity.User;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignUpRequestDto dto);

    UserInfoDto toUserInfoDto(User user);

    AuthResponseDto toAuthResponseDto(User user);
}
