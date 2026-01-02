package com.STFAS.service;

import com.STFAS.dto.auth.request.AuthRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.entity.User;
import com.STFAS.entity.Warehouse;
import com.STFAS.enums.Role;
import com.STFAS.mapper.UserMapper;
import com.STFAS.repository.UserRepository;
import com.STFAS.repository.WarehouseRepository;
import com.STFAS.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private AuthService authService;

    private User user;
    private AuthRequestDto authRequestDto;
    private AuthResponseDto authResponseDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-123");
        user.setEmail("john@example.com");
        user.setName("John Doe");
        user.setPassword("encodedPassword");
        user.setRole(Role.GESTIONNAIRE);

        authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("john@example.com");
        authRequestDto.setPassword("plainPassword");
        authRequestDto.setWarehouse(null);

        authResponseDto = new AuthResponseDto();
        authResponseDto.setId("user-123");
        authResponseDto.setRole(Role.GESTIONNAIRE);
        authResponseDto.setToken("jwt-token-123");

        authentication = mock(Authentication.class);
    }

    // ==================== login Tests ====================
    @Test
    @DisplayName("login - should successfully authenticate user and return token")
    void testLoginSuccess() {
        // Arrange
        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(authentication)).thenReturn("jwt-token-123");
        when(userMapper.toAuthResponseDto(user)).thenReturn(authResponseDto);

        // Act
        AuthResponseDto result = authService.login(authRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token-123", result.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(jwtUtils, times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("login - should throw exception when user credentials are invalid")
    void testLoginInvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
            authService.login(authRequestDto)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("login - should throw exception when user not found after authentication")
    void testLoginUserNotFound() {
        // Arrange
        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            authService.login(authRequestDto)
        );
        assertEquals("User not found", exception.getMessage());
        verify(jwtUtils, never()).generateToken(any(Authentication.class));
    }

    @Test
    @DisplayName("login - should set security context with authentication")
    void testLoginSetsSecurityContext() {
        // Arrange
        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(authentication)).thenReturn("jwt-token-123");
        when(userMapper.toAuthResponseDto(user)).thenReturn(authResponseDto);

        // Act
        authService.login(authRequestDto);

        // Assert
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login - should handle GESTIONNAIRE with warehouse assignment")
    void testLoginGestionnaireWithWarehouse() {
        // Arrange
        authRequestDto.setWarehouse("warehouse-123");
        Warehouse warehouse = new Warehouse();
        warehouse.setId("warehouse-123");

        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(warehouseRepository.findById("warehouse-123")).thenReturn(Optional.of(warehouse));
        when(jwtUtils.generateToken(authentication)).thenReturn("jwt-token-123");
        when(userMapper.toAuthResponseDto(user)).thenReturn(authResponseDto);

        // Act
        AuthResponseDto result = authService.login(authRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("user-123", result.getId());
    }

    @Test
    @DisplayName("login - should generate valid JWT token")
    void testLoginTokenGeneration() {
        // Arrange
        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(authentication)).thenReturn("jwt-token-123");
        when(userMapper.toAuthResponseDto(user)).thenReturn(authResponseDto);

        // Act
        AuthResponseDto result = authService.login(authRequestDto);

        // Assert
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isEmpty());
        assertEquals("jwt-token-123", result.getToken());
    }

    @Test
    @DisplayName("login - should map user to response correctly")
    void testLoginUserMapping() {
        // Arrange
        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(authentication)).thenReturn("jwt-token-123");
        when(userMapper.toAuthResponseDto(user)).thenReturn(authResponseDto);

        // Act
        AuthResponseDto result = authService.login(authRequestDto);

        // Assert
        assertEquals("user-123", result.getId());
        assertEquals(Role.GESTIONNAIRE, result.getRole());
        verify(userMapper, times(1)).toAuthResponseDto(user);
    }

    @Test
    @DisplayName("login - should handle null warehouse in request")
    void testLoginNullWarehouse() {
        // Arrange
        authRequestDto.setWarehouse(null);
        mockSecurityContext();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(authentication)).thenReturn("jwt-token-123");
        when(userMapper.toAuthResponseDto(user)).thenReturn(authResponseDto);

        // Act
        AuthResponseDto result = authService.login(authRequestDto);

        // Assert
        assertNotNull(result);
        verify(warehouseRepository, never()).findById(anyString());
    }

    // ==================== getUserInfo Tests ====================
    @Test
    @DisplayName("getUserInfo - should return null (not implemented)")
    void testGetUserInfo() {
        // Act
        var result = authService.getUserInfo("user-123");

        // Assert
        assertNull(result);
    }

    // ==================== Helper Methods ====================
    private void mockSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
