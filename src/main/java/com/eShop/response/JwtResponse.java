package com.eShop.response;

import com.eShop.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private UserDto user;

    private String token;

    public JwtResponse(Long id, String jwt) {
        this.user = new UserDto();
        this.user.setId(id);
        this.token = jwt;
    }
}