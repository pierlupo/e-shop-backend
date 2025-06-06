package com.eShop.service.user;

import com.eShop.dto.UserDto;
import com.eShop.model.User;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IUserService {

    User getUserById(Long userId);
    Optional<User> findByEmail(String email);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);
    UserDto convertToUserDto(User user);
    User getAuthenticatedUser();
    String uploadAvatar(Long userId, MultipartFile file) throws IOException;
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    boolean resetPasswordWithToken(String token, String newPassword);
    void saveUser(User user);
}