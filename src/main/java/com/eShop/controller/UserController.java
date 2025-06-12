package com.eShop.controller;


import com.eShop.dto.UserDto;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import com.eShop.request.ChangePasswordRequest;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.UserUpdateRequest;
import com.eShop.response.ApiResponse;
import com.eShop.service.email.IEmailService;
import com.eShop.service.email.IEmailVerificationService;
import com.eShop.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final IUserService userService;

    private final IEmailVerificationService emailVerificationService;

    private final IEmailService emailService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        log.info("getAllUsers");
        try {
            List<User> users = userService.getAllUsers();
            List<UserDto> userDtos = users.stream()
                    .map(userService::convertToUserDto)
                    .toList();
            return ResponseEntity.ok(new ApiResponse("Fetched all users successfully", userDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch users", null));
        }
    }


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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add")
    public ResponseEntity<ApiResponse> createUserByAdmin(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUserWithoutPassword(request);
            user.setEmailVerified(true);

            EmailVerificationToken token = emailVerificationService.createOrReplaceToken(user);
            emailService.sendPasswordResetEmail(user, token.getToken());

            UserDto userDto = userService.convertToUserDto(user);
            return ResponseEntity.ok(new ApiResponse("User created by admin. Password reset email sent.", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
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

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        log.info("Received password change request for user ID: {}", userId);
        boolean success = userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(new ApiResponse("Password changed successfully", null));
        } else {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse("Current password is incorrect", null));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse("If this email exists, a reset link has been sent.", null));
        }
        User user = userOptional.get();
        EmailVerificationToken token = emailVerificationService.createOrReplaceToken(user);
        emailService.sendPasswordResetEmail(user, token.getToken());
        return ResponseEntity.ok(new ApiResponse("Reset password link sent successfully", null));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        boolean success = userService.resetPasswordWithToken(token, newPassword);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invalid or expired token", null));
        }
        return ResponseEntity.ok(new ApiResponse("Password reset successful", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getAuthenticatedUser() {
        try {
            User user = userService.getAuthenticatedUser();
            UserDto userDto = userService.convertToUserDto(user);
            return ResponseEntity.ok(new ApiResponse("Fetched authenticated user", userDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch authenticated user", null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody List<String> roleNames) {

        try {
            User user = userService.updateUserRoles(userId, roleNames);
            UserDto userDto = userService.convertToUserDto(user);
            return ResponseEntity.ok(new ApiResponse("User roles updated successfully", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}