package com.eShop.service.user;

import com.eShop.dto.*;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.User;
import com.eShop.repository.UserRepository;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found!"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request).filter(user-> !userRepository.existsByEmail(request.getEmail()))
                .map(req-> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setFirstname(request.getFirstname());
                    user.setLastname(request.getLastname());
                    return userRepository.save(user);
                }).orElseThrow(()-> new AlreadyExistsException("This " + request.getEmail() + " already exists!"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId)
                .map(existingUser-> {
                    existingUser.setFirstname(request.getFirstname());
                    existingUser.setLastname(request.getLastname());
                    return userRepository.save(existingUser);
                }).orElseThrow(()->  new ResourceNotFoundException("User not found!"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(userRepository::delete, ()-> {
                    throw new ResourceNotFoundException("User not found!");
                });
    }

    @Override
    public UserDto convertToUserDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        // Manually patch cart items (ModelMapper can't handle nested collections easily)
        if (user.getCart() != null && user.getCart().getItems() != null) {
            CartDto cartDto = userDto.getCart(); // already mapped base fields
            if (cartDto == null) {
                cartDto = new CartDto();
                cartDto.setCartId(user.getCart().getId());
                cartDto.setTotalAmount(user.getCart().getTotalAmount());
            }
            Set<CartItemDto> cartItems = user.getCart().getItems().stream().map(item -> {
                CartItemDto dto = new CartItemDto();
                dto.setItemId(item.getProduct().getId());
                dto.setQuantity(item.getQuantity());
                dto.setUnitPrice(item.getUnitPrice());
                ProductDto productDto = modelMapper.map(item.getProduct(), ProductDto.class);
                dto.setProduct(productDto);
                return dto;
            }).collect(java.util.stream.Collectors.toSet());

            cartDto.setItems(cartItems);
            userDto.setCart(cartDto);
        }
        // Optional: Patch brand in order items if ModelMapper doesn’t handle it correctly
        if (user.getOrders() != null) {
            userDto.getOrders().forEach(orderDto -> {
                user.getOrders().stream()
                        .filter(order -> order.getOrderId().equals(orderDto.getOrderId()))
                        .findFirst()
                        .ifPresent(order -> {
                            List<OrderItemDto> fixedItems = order.getOrderItems().stream().map(item -> {
                                OrderItemDto itemDto = new OrderItemDto();
                                itemDto.setProductId(item.getProduct().getId());
                                itemDto.setProductName(item.getProduct().getProductName());
                                itemDto.setBrand(item.getProduct().getBrand());
                                itemDto.setQuantity(item.getQuantity());
                                itemDto.setPrice(item.getPrice());
                                return itemDto;
                            }).toList();
                            orderDto.setItems(fixedItems);
                        });
            });
        }

        return userDto;
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        String uploadDir = "uploads/avatars";
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }
        String filename = "avatar_user_" + userId + "_" + System.currentTimeMillis() + (fileExtension.isEmpty() ? "" : "." + fileExtension);
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, file.getBytes());
        String avatarUrl = "/" + uploadDir + "/" + filename;
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }
}