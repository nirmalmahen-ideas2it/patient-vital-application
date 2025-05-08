package com.ideas2it.training.patient.vital.web.rest.controller;

import com.ideas2it.training.patient.vital.dto.auth.LoginRequest;
import com.ideas2it.training.patient.vital.dto.auth.TokenResponse;
import com.ideas2it.training.patient.vital.security.TokenContextHolder;
import com.ideas2it.training.patient.vital.webclient.PatientClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * REST controller for authentication.
 *
 * <p>This controller provides an endpoint for user authentication. It validates
 * user credentials using the {@link PatientClient} and generates an access
 *
 * <p>Example usage:</p>
 * <pre>
 * POST /api/authenticate
 * {
 *   "username": "user",
 *   "password": "password"
 * }
 * </pre>
 *
 * @author Alagu Nirmal Mahendran
 * @version 1.0
 * @since 06/05/2025
 */
@RestController
@RequestMapping("/api/authenticate")
public class AuthController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PatientClient patientClient;
    @Value("${token.expiry.seconds:300}") // 5 minutes default
    private long tokenTtl;

    /**
     * Constructs an instance of {@link AuthController}.
     *
     * @param patientClient the client for validating user credentials
     *                      //     * @param keycloakTokenService the service for generating access tokens
     */
    public AuthController(RedisTemplate<String, Object> redisTemplate, PatientClient patientClient
    ) {
        this.redisTemplate = redisTemplate;
        this.patientClient = patientClient;
    }

    /**
     * Authenticates a user.
     *
     * <p>This method validates the provided login request using the user validation
     * service. If the credentials are valid, it generates an access token and returns
     * it in the response. Otherwise, it returns an unauthorized status.</p>
     *
     * @param request the login request containing user credentials
     * @return a response entity containing the access token or an error message
     */
    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            TokenContextHolder.setToken(token);
            TokenResponse result = patientClient.authenticate(request);
            if (result.getAccessToken() != null && !result.getAccessToken().isEmpty()) {
                redisTemplate.opsForValue().set(token, true, Duration.ofSeconds(tokenTtl));
                return ResponseEntity.ok(new TokenResponse(token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } finally {
            TokenContextHolder.clear();
        }
    }

}
