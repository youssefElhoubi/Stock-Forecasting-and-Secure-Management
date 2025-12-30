package com.STFAS.service.repository;

import com.STFAS.dto.auth.request.AuthRequestDto;
import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;

public interface Auth {
    AuthResponseDto login(AuthRequestDto authRequestDto);

    UserInfoDto  getUserInfo(String id);
}
