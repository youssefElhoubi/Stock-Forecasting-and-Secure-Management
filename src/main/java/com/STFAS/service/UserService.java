package com.STFAS.service;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import com.STFAS.entity.User;
import com.STFAS.entity.Warehouse;
import com.STFAS.enums.Role;
import com.STFAS.exception.ResourceNotFoundException;
import com.STFAS.mapper.UserMapper;
import com.STFAS.repository.UserRepository;
import com.STFAS.repository.WarehouseRepository;
import com.STFAS.security.JwtUtils;
import com.STFAS.service.repository.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final WarehouseRepository warehouseRepository;
    @Override
    public AuthResponseDto createUser(SignUpRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with email already exists");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userMapper.toAuthResponseDto(user);
    }

    @Override
    public void assignWarehouseToUser(String userId, String warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setWarehouse(warehouse);
    }

    @Override
    public UserInfoDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserInfoDto(user);
    }

    @Override
    public UserInfoDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserInfoDto(user);
    }

    @Override
    public List<UserInfoDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return  users.stream().map(userMapper::toUserInfoDto).toList();
    }

    @Override
    public List<UserInfoDto> getAllManagers() {
        List<User> users = userRepository.findAll();
        return  users.stream().filter((u)->u.getRole()== Role.USER).map(userMapper::toUserInfoDto).toList();
    }

    @Override
    public UserInfoDto updateUser(String id, SignUpRequestDto request) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return userMapper.toUserInfoDto(user);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
