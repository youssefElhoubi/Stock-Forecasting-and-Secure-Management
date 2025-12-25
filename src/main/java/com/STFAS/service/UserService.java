package com.STFAS.service;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import com.STFAS.service.repository.UserServiceInterface;

import java.util.List;

public class UserService implements UserServiceInterface {
    @Override
    public UserInfoDto createUser(SignUpRequestDto request) {
        return null;
    }

    @Override
    public void assignWarehouseToUser(String userId, String warehouseId) {

    }

    @Override
    public UserInfoDto getUserById(String id) {
        return null;
    }

    @Override
    public UserInfoDto getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<UserInfoDto> getAllUsers() {
        return List.of();
    }

    @Override
    public List<UserInfoDto> getAllManagers() {
        return List.of();
    }

    @Override
    public UserInfoDto updateUser(String id, SignUpRequestDto request) {
        return null;
    }

    @Override
    public void deleteUser(String id) {

    }
}
