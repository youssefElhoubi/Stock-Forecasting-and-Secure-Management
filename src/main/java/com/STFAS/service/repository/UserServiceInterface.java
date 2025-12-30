package com.STFAS.service.repository;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.UserInfoDto;

import java.util.List;

public interface UserServiceInterface {

    UserInfoDto createUser(SignUpRequestDto request);
    void assignWarehouseToUser(String userId, String warehouseId);
    UserInfoDto getUserById(String id);
    UserInfoDto getUserByEmail(String email);
    List<UserInfoDto> getAllUsers();
    List<UserInfoDto> getAllManagers();
    UserInfoDto updateUser(String id, SignUpRequestDto request);
    void deleteUser(String id);
}
