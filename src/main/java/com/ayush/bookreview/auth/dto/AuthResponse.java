package com.ayush.bookreview.auth.dto;

import lombok.Getter;

@Getter
public class AuthResponse {

    private final String accessToken;
    private final String type = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}