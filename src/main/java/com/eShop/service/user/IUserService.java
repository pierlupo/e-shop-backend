package com.eShop.service.user;

import com.eShop.dto.UserDto;
import com.eShop.model.User;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.UserUpdateRequest;

public interface IUserService {

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);
    UserDto convertToUserDto(User user);
    User getAuthenticatedUser();
}