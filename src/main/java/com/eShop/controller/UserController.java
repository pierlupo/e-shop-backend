package com.eShop.controller;


import com.eShop.dto.UserDto;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.User;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.UserUpdateRequest;
import com.eShop.response.ApiResponse;
import com.eShop.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertToUserDto(user);
            return ResponseEntity.ok(new ApiResponse("Got user successfully", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertToUserDto(user);
            return ResponseEntity.ok(new ApiResponse("Created user successfully", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId) {
        try {
            User user = userService.updateUser(request, userId);
            UserDto userDto = userService.convertToUserDto(user);
            return ResponseEntity.ok(new ApiResponse("Updated user successfully", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("Deleted user successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<ApiResponse> uploadAvatar(@PathVariable Long userId, @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            String avatarUrl = userService.uploadAvatar(userId, avatarFile);
            return ResponseEntity.ok(new ApiResponse("Avatar uploaded successfully", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
}