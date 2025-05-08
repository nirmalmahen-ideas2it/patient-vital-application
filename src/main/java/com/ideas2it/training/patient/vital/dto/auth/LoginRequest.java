package com.ideas2it.training.patient.vital.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
