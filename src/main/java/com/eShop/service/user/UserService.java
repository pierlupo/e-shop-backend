package com.eShop.service.user;

import com.eShop.dto.*;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.EmailVerificationToken;
import com.eShop.model.Role;
import com.eShop.model.User;
import com.eShop.repository.RoleRepository;
import com.eShop.repository.UserRepository;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.UserUpdateRequest;
import com.eShop.service.email.EmailService;
import com.eShop.service.email.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found!"));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
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
                    user.setEmailVerified(false);
                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));
                    user.setRoles(List.of(userRole));
                    return userRepository.save(user);
                }).orElseThrow(()-> new AlreadyExistsException("This " + request.getEmail() + " already exists!"));
    }

    @Override
    public User createUserWithoutPassword(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("This " + request.getEmail() + " already exists!");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));
        user.setRoles(List.of(userRole));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId)
                .map(existingUser-> {
                    existingUser.setFirstname(request.getFirstname());
                    existingUser.setLastname(request.getLastname());
                    if (!existingUser.getEmail().equalsIgnoreCase(request.getEmail())) {
                        existingUser.setEmail(request.getEmail());
                        existingUser.setEmailVerified(false);
                        var token = emailVerificationService.createOrReplaceToken(existingUser);
                        emailService.sendVerificationEmail(existingUser, token.getToken());
                    }
                    return userRepository.save(existingUser);
                }).orElseThrow(()->  new ResourceNotFoundException("User not found!"));
    }

    @Override
    public User updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new IllegalArgumentException("One or more roles are invalid.");
        }
        user.setRoles(roles);
        return userRepository.save(user);
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
        if (user.getRoles() != null) {
            List<RoleDto> roleDtos = user.getRoles().stream()
                    .map(role -> new RoleDto(role.getId(), role.getName()))
                    .toList();
            userDto.setRoles(roleDtos);
        }
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

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean resetPasswordWithToken(String token, String newPassword) {
        Optional<EmailVerificationToken> optionalToken = emailVerificationService.getToken(token);
        if (optionalToken.isEmpty()) {
            return false;
        }
        EmailVerificationToken tokenEntity = optionalToken.get();
        if (emailVerificationService.isTokenExpired(tokenEntity)) {
            return false;
        }
        User user = tokenEntity.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
        }
        log.info("User {} verified email via password reset token", user.getEmail());
        userRepository.save(user);
        emailVerificationService.deleteToken(tokenEntity);
        return true;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}