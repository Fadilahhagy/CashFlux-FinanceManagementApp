package com.cashflux.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private Long expiresIn;
}
