package com.STFAS.aop.aspect;

import com.STFAS.security.CustomUserDetailsService;
import com.STFAS.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class RoleCheckAspect {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;


}
