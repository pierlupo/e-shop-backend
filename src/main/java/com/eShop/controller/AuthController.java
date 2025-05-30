package com.eShop.controller;


import com.eShop.dto.UserDto;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.model.User;
import com.eShop.request.CreateUserRequest;
import com.eShop.request.LoginRequest;
import com.eShop.response.ApiResponse;
import com.eShop.response.JwtResponse;
import com.eShop.security.jwt.JwtUtils;
import com.eShop.security.user.ShopUserDetails;
import com.eShop.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            User user = userService.getUserById(userDetails.getId());
            UserDto userDto = userService.convertToUserDto(user);
            JwtResponse jwtResponse = new JwtResponse(userDto, jwt);
            return ResponseEntity.ok(new ApiResponse("Login successful", jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse( e.getMessage(), null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            // 1. Create user
            User newUser = userService.createUser(request);
            // 2. Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            // 3. Set context and generate token
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            // 4. Return response
            return ResponseEntity.ok(new ApiResponse("User registered successfully",
                    new JwtResponse(newUser, jwt, modelMapper)));
        } catch (AlreadyExistsException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(ex.getMessage(), null));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@AuthenticationPrincipal ShopUserDetails userDetails) {
        User user = userService.getUserById(userDetails.getId());
        UserDto userDto = userService.convertToUserDto(user);
        return ResponseEntity.ok(new ApiResponse("Token is valid", userDto));
    }
}