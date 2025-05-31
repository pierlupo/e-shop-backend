package com.eShop.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
public class UserDto {

    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private Collection<RoleDto> roles;

    private List<OrderDto> orders;

    private CartDto cart;

    private String avatarUrl;

    private LocalDateTime registrationDate;

}