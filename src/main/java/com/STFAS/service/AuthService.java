package com.STFAS.service;

import com.STFAS.dto.auth.request.AuthRequestDto;
import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import com.STFAS.repository.UserRepository;
import com.STFAS.service.repository.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements Auth {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        return null;
    }

    @Override
    public AuthResponseDto signup(SignUpRequestDto authRequestDto) {
        return null;
    }

    @Override
    public UserInfoDto getUserInfo(String id) {
        return null;
    }
}
