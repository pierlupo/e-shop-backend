package com.eShop.controller;


import com.eShop.dto.UserDto;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.model.EmailVerificationToken;
import com.eShop.model.User;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.LoginRequest;
import com.eShop.response.ApiResponse;
import com.eShop.response.JwtResponse;
import com.eShop.security.jwt.JwtUtils;
import com.eShop.security.user.ShopUserDetails;
import com.eShop.service.email.EmailVerificationService;
import com.eShop.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    private final EmailVerificationService emailVerificationService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse jwtResponse = authenticateAndGenerateToken(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse("Login successful", jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            userService.createUser(request);
            JwtResponse jwtResponse = authenticateAndGenerateToken(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse("User registered successfully", jwtResponse));
        } catch (AlreadyExistsException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(ex.getMessage(), null));
        }
    }

    private JwtResponse authenticateAndGenerateToken(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateTokenForUser(authentication);
        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        UserDto userDto = userService.convertToUserDto(user);
        return new JwtResponse(userDto, jwt);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@AuthenticationPrincipal ShopUserDetails userDetails) {
        User user = userService.getUserById(userDetails.getId());
        UserDto userDto = userService.convertToUserDto(user);
        return ResponseEntity.ok(new ApiResponse("Token is valid", userDto));
    }

    @PostMapping("/{userId}/verify-email")
    public ResponseEntity<?> sendVerificationEmail(@PathVariable Long userId) {
        emailVerificationService.createAndSendToken(userId);
        return ResponseEntity.ok(new ApiResponse("Verification email sent", null));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        Optional<EmailVerificationToken> optionalToken = emailVerificationService.getToken(token);
        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid token", null));
        }
        EmailVerificationToken emailToken = optionalToken.get();
        if (emailVerificationService.isTokenExpired(emailToken)) {
            return ResponseEntity.badRequest().body(new ApiResponse("Token expired", null));
        }
        User user = emailToken.getUser();
        user.setEmailVerified(true);
        userService.saveUser(user);
        emailVerificationService.deleteToken(emailToken);
        return ResponseEntity.ok(new ApiResponse("Email verified successfully", null));
    }

}