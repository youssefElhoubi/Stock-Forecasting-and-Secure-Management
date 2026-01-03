package com.STFAS.service;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import com.STFAS.entity.User;
import com.STFAS.entity.Warehouse;
import com.STFAS.enums.Role;
import com.STFAS.exception.ResourceNotFoundException;
import com.STFAS.mapper.UserMapper;
import com.STFAS.repository.UserRepository;
import com.STFAS.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private SignUpRequestDto signUpRequestDto;
    private UserInfoDto userInfoDto;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-123");
        user.setEmail("john@example.com");
        user.setName("John Doe");
        user.setPassword("encodedPassword");
        user.setRole(Role.GESTIONNAIRE);

        signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setEmail("john@example.com");
        signUpRequestDto.setName("John Doe");
        signUpRequestDto.setPassword("plainPassword");
        signUpRequestDto.setRole(Role.GESTIONNAIRE);

        userInfoDto = new UserInfoDto();
        userInfoDto.setId("user-123");
        userInfoDto.setEmail("john@example.com");
        userInfoDto.setName("John Doe");
        userInfoDto.setRole(Role.GESTIONNAIRE);

        warehouse = new Warehouse();
        warehouse.setId("warehouse-123");
        warehouse.setName("Main Warehouse");
    }

    // ==================== createUser Tests ====================
    @Test
    @DisplayName("createUser - should successfully create a new user with default role GESTIONNAIRE")
    void testCreateUserSuccess() {
        // Arrange
        when(userRepository.findByEmail(signUpRequestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntity(signUpRequestDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserInfoDto(user)).thenReturn(userInfoDto);

        // Act
        UserInfoDto result = userService.createUser(signUpRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        assertEquals(Role.GESTIONNAIRE, result.getRole());
        verify(userRepository, times(1)).findByEmail(signUpRequestDto.getEmail());
        verify(passwordEncoder, times(1)).encode(signUpRequestDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - should throw exception when user with email already exists")
    void testCreateUserWithDuplicateEmail() {
        // Arrange
        when(userRepository.findByEmail(signUpRequestDto.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userService.createUser(signUpRequestDto)
        );
        assertEquals("User with email already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(signUpRequestDto.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - should set default role to GESTIONNAIRE when role is null")
    void testCreateUserDefaultRole() {
        // Arrange
        signUpRequestDto.setRole(null);
        user.setRole(Role.GESTIONNAIRE);
        when(userRepository.findByEmail(signUpRequestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntity(signUpRequestDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserInfoDto(user)).thenReturn(userInfoDto);

        // Act
        UserInfoDto result = userService.createUser(signUpRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(Role.GESTIONNAIRE, result.getRole());
    }

    // ==================== getUserById Tests ====================
    @Test
    @DisplayName("getUserById - should return user when exists")
    void testGetUserByIdSuccess() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfoDto(user)).thenReturn(userInfoDto);

        // Act
        UserInfoDto result = userService.getUserById("user-123");

        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findById("user-123");
    }

    @Test
    @DisplayName("getUserById - should throw ResourceNotFoundException when user not found")
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserById("user-999")
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById("user-999");
    }

    // ==================== getUserByEmail Tests ====================
    @Test
    @DisplayName("getUserByEmail - should return user when email exists")
    void testGetUserByEmailSuccess() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfoDto(user)).thenReturn(userInfoDto);

        // Act
        UserInfoDto result = userService.getUserByEmail("john@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("getUserByEmail - should throw ResourceNotFoundException when email not found")
    void testGetUserByEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserByEmail("nonexistent@example.com")
        );
        assertEquals("User not found", exception.getMessage());
    }

    // ==================== getAllUsers Tests ====================
    @Test
    @DisplayName("getAllUsers - should return all users")
    void testGetAllUsersSuccess() {
        // Arrange
        User user2 = new User();
        user2.setId("user-456");
        user2.setEmail("jane@example.com");
        user2.setRole(Role.ADMIN);

        UserInfoDto userInfoDto2 = new UserInfoDto();
        userInfoDto2.setId("user-456");
        userInfoDto2.setEmail("jane@example.com");
        userInfoDto2.setRole(Role.ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.toUserInfoDto(user)).thenReturn(userInfoDto);
        when(userMapper.toUserInfoDto(user2)).thenReturn(userInfoDto2);

        // Act
        List<UserInfoDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("john@example.com", result.get(0).getEmail());
        assertEquals("jane@example.com", result.get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllUsers - should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserInfoDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getAllManagers Tests ====================
    @Test
    @DisplayName("getAllManagers - should return only GESTIONNAIRE role users")
    void testGetAllManagersSuccess() {
        // Arrange
        User admin = new User();
        admin.setId("admin-123");
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);

        UserInfoDto adminInfoDto = new UserInfoDto();
        adminInfoDto.setId("admin-123");
        adminInfoDto.setEmail("admin@example.com");
        adminInfoDto.setRole(Role.ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user, admin));
        lenient().when(userMapper.toUserInfoDto(user)).thenReturn(userInfoDto);
        lenient().when(userMapper.toUserInfoDto(admin)).thenReturn(adminInfoDto);

        // Act
        List<UserInfoDto> result = userService.getAllManagers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Role.GESTIONNAIRE, result.get(0).getRole());
    }

    @Test
    @DisplayName("getAllManagers - should return empty list when no managers exist")
    void testGetAllManagersEmpty() {
        // Arrange
        User admin = new User();
        admin.setRole(Role.ADMIN);
        when(userRepository.findAll()).thenReturn(List.of(admin));
        lenient().when(userMapper.toUserInfoDto(admin)).thenReturn(new UserInfoDto());

        // Act
        List<UserInfoDto> result = userService.getAllManagers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== updateUser Tests ====================
    @Test
    @DisplayName("updateUser - should successfully update user details")
    void testUpdateUserSuccess() {
        // Arrange
        SignUpRequestDto updateRequest = new SignUpRequestDto();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPassword("newPassword");

        user.setName("John Updated");
        user.setEmail("john.updated@example.com");

        UserInfoDto updatedInfoDto = new UserInfoDto();
        updatedInfoDto.setId("user-123");
        updatedInfoDto.setName("John Updated");
        updatedInfoDto.setEmail("john.updated@example.com");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfoDto(user)).thenReturn(updatedInfoDto);

        // Act
        UserInfoDto result = userService.updateUser("user-123", updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        verify(userRepository, times(1)).findById("user-123");
    }

    @Test
    @DisplayName("updateUser - should throw ResourceNotFoundException when user not found")
    void testUpdateUserNotFound() {
        // Arrange
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.updateUser("user-999", signUpRequestDto)
        );
        assertEquals("User not found", exception.getMessage());
    }

    // ==================== assignWarehouseToUser Tests ====================
    @Test
    @DisplayName("assignWarehouseToUser - should successfully assign warehouse to user")
    void testAssignWarehouseToUserSuccess() {
        // Arrange
        when(warehouseRepository.findById("warehouse-123")).thenReturn(Optional.of(warehouse));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        // Act
        userService.assignWarehouseToUser("user-123", "warehouse-123");

        // Assert
        assertEquals("warehouse-123", user.getWarehouse().getId());
        verify(warehouseRepository, times(1)).findById("warehouse-123");
        verify(userRepository, times(1)).findById("user-123");
    }

    @Test
    @DisplayName("assignWarehouseToUser - should throw ResourceNotFoundException when warehouse not found")
    void testAssignWarehouseToUserWarehouseNotFound() {
        // Arrange
        when(warehouseRepository.findById("warehouse-999")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.assignWarehouseToUser("user-123", "warehouse-999")
        );
        assertEquals("Warehouse not found", exception.getMessage());
    }

    @Test
    @DisplayName("assignWarehouseToUser - should throw ResourceNotFoundException when user not found")
    void testAssignWarehouseToUserUserNotFound() {
        // Arrange
        when(warehouseRepository.findById("warehouse-123")).thenReturn(Optional.of(warehouse));
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.assignWarehouseToUser("user-999", "warehouse-123")
        );
        assertEquals("User not found", exception.getMessage());
    }

    // ==================== deleteUser Tests ====================
    @Test
    @DisplayName("deleteUser - should successfully delete user")
    void testDeleteUserSuccess() {
        // Arrange
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser("user-123");

        // Assert
        verify(userRepository, times(1)).findById("user-123");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("deleteUser - should throw ResourceNotFoundException when user not found")
    void testDeleteUserNotFound() {
        // Arrange
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.deleteUser("user-999")
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }
}
