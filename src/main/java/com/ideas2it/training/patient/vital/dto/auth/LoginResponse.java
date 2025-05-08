package com.ideas2it.training.patient.vital.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private LoginResult status;
    private String message;
}
