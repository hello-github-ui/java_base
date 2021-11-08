package com.example.model;

import lombok.Data;

@Data
public class User {
    private int id;

    private String username;

    private String nickname;

    private String password;

    private int status;

    private String email;

    private String userface;

    private String regTime;
}
