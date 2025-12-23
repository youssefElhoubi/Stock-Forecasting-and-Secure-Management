package com.STFAS.controller;

import com.STFAS.dto.auth.request.AuthRequestDto;
import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto){
        return ResponseEntity.ok(service.login(authRequestDto));
    }
    @PostMapping("signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody SignUpRequestDto authRequestDto){
        return ResponseEntity.ok(service.signup(authRequestDto));
    }
}
