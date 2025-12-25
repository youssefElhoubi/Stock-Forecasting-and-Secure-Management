package com.STFAS.aop.aspect;

import com.STFAS.aop.interfaces.HaveAccess;
import com.STFAS.exception.BusinessRuleViolationException;
import com.STFAS.security.JwtUtils;
import com.STFAS.service.WarehouseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class RoleCheckAspect {
    private final JwtUtils jwtUtils;
    private final WarehouseService warehouseService;

    @Before("@annotation(haveAccess)")
    public void beforeHaveAccess(JoinPoint joinPoint, HaveAccess haveAccess) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessRuleViolationException("Invalid or expired JWT token");
        }
        List<String> userRoles = jwtUtils.extractRoles(token);
        String email = jwtUtils.extractUsername(token);

        if (userRoles.contains("ADMIN")) {
            return;
        }
        String paramToCheck = haveAccess.id();

        // Only run this if the user put a param name in the annotation
        if (!paramToCheck.isEmpty()) {
            Object paramValue = getParameterValueByName((ProceedingJoinPoint) joinPoint, paramToCheck);

            if (paramValue == null) {
                throw new BusinessRuleViolationException("Security parameter '" + paramToCheck + "' not found");
            }
        }

    }

    private Object getParameterValueByName(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (parameterNames == null) {
            log.warn("Parameter names are missing. Compile with -parameters flag.");
            return null;
        }

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                return args[i];
            }
        }
        return null; // Parameter not found
    }

}
