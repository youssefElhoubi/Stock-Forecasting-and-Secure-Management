package com.STFAS.controller;

import com.STFAS.dto.auth.request.SignUpRequestDto;
import com.STFAS.dto.auth.response.AuthResponseDto;
import com.STFAS.dto.auth.response.UserInfoDto;
import com.STFAS.service.repository.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceInterface userService;

    @PostMapping
    public ResponseEntity<UserInfoDto> createUser(
            @RequestBody @Valid SignUpRequestDto request
    ) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserInfoDto> updateUser(
            @PathVariable String id,
            @RequestBody SignUpRequestDto request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{userId}/warehouse/{warehouseId}")
    public ResponseEntity<Void> assignWarehouseToUser(
            @PathVariable String userId,
            @PathVariable String warehouseId
    ) {
        userService.assignWarehouseToUser(userId, warehouseId);
        return ResponseEntity.ok().build();
    }



}
