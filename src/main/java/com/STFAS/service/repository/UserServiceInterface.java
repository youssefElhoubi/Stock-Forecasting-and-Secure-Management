package com.STFAS.service.repository;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;

import java.util.List;

public interface UserServiceInterface {

    /**
     * Creates a new User (usually a MANAGER) in the system.
     * Access: ADMIN only.
     * Logic: Encrypts password, sets default active status.
     */
    UserInfoDto createUser(SignUpRequestDto request);

    /**
     * Assigns a specific Warehouse to a Manager.
     * Access: ADMIN only.
     * Validation: Ensures the user is a MANAGER and the warehouse exists.
     * @param userId The ID of the manager.
     * @param warehouseId The ID of the warehouse.
     */
    void assignWarehouseToUser(String userId, String warehouseId);

    /**
     * Retrieves a user by their unique ID.
     * Access: ADMIN (or the User themselves).
     */
    UserInfoDto getUserById(String id);

    /**
     * Retrieves a user by their email address.
     * Access: Internal authentication or ADMIN.
     */
    UserInfoDto getUserByEmail(String email);

    /**
     * Retrieves a list of all users in the system.
     * Access: ADMIN only.
     */
    List<UserInfoDto> getAllUsers();

    /**
     * Retrieves all users who have the role MANAGER.
     * Access: ADMIN only.
     * Useful for seeing which managers are available or assigned.
     */
    List<UserInfoDto> getAllManagers();

    /**
     * Updates user details (Name, Email, Active Status).
     * Access: ADMIN.
     * Note: Password updates usually have a separate dedicated method.
     */
    UserInfoDto updateUser(String id, SignUpRequestDto request);

    /**
     * Permanently removes a user or deactivates them.
     * Access: ADMIN only.
     */
    void deleteUser(String id);
}
