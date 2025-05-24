package com.eShop.request;


import lombok.Data;

@Data
public class CreateUserRequest {

    private String firstname;

    private String lastname;

    private String email;

    private String password;
}