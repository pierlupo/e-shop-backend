package com.eShop.response;

import com.eShop.dto.UserDto;
import com.eShop.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private UserDto user;

    private String token;

    public JwtResponse(User user, String jwt, ModelMapper modelMapper) {
        this.user = modelMapper.map(user, UserDto.class);
        this.token = jwt;
    }

}