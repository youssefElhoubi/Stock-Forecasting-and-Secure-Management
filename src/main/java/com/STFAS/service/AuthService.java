package com.STFAS.service;

import com.STFAS.dto.auth.request.AuthRequestDto;
import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import com.STFAS.entity.User;
import com.STFAS.mapper.UserMapper;
import com.STFAS.repository.UserRepository;
import com.STFAS.security.JwtUtils;
import com.STFAS.service.repository.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements Auth {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDto login(AuthRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(authentication);

        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        AuthResponseDto responseDto = userMapper.toAuthResponseDto(user);

        responseDto.setToken(token);
        
        return responseDto;
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
