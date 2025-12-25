package com.STFAS.aop.aspect;
import com.STFAS.aop.interfaces.HaveAccess;
import com.STFAS.entity.User;
import com.STFAS.entity.Warehouse;
import com.STFAS.exception.BusinessRuleViolationException;
import com.STFAS.exception.ResourceNotFoundException;
import com.STFAS.repository.UserRepository;
import com.STFAS.repository.WarehouseRepository;
import com.STFAS.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint; // Use this, not ProceedingJoinPoint
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
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository; // Renamed from userService (it's a Repo)

    @Before("@annotation(haveAccess)")
    public void beforeHaveAccess(JoinPoint joinPoint, HaveAccess haveAccess) {
        // 1. Validate Token
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessRuleViolationException("Missing Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessRuleViolationException("Invalid or expired JWT token");
        }

        List<String> userRoles = jwtUtils.extractRoles(token);
        String email = jwtUtils.extractUsername(token);

        if (userRoles.contains("ADMIN")) {
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String paramToCheck = haveAccess.id();

        if (!paramToCheck.isEmpty()) {
            // FIX: Removed (ProceedingJoinPoint) cast
            Object rawValue = getParameterValueByName(joinPoint, paramToCheck);

            if (rawValue == null) {
                throw new BusinessRuleViolationException("Security parameter '" + paramToCheck + "' not found");
            }

            String warehouseId = rawValue.toString();

            Warehouse warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse with id " + warehouseId + " not found"));

            if (!user.getWarehouse().getId().equals(warehouse.getId())) {
                    throw new BusinessRuleViolationException("You do not have rights for this warehouse.");
            }
        }
    }

    private Object getParameterValueByName(JoinPoint joinPoint, String paramName) {
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
        return null;
    }
}