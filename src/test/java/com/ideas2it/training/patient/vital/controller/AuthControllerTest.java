package com.ideas2it.training.patient.vital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas2it.training.patient.vital.config.SecurityConfig;
import com.ideas2it.training.patient.vital.dto.auth.LoginRequest;
import com.ideas2it.training.patient.vital.dto.auth.TokenResponse;
import com.ideas2it.training.patient.vital.web.rest.controller.AuthController;
import com.ideas2it.training.patient.vital.webclient.PatientClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {OAuth2ResourceServerAutoConfiguration.class})
@Import(SecurityConfig.class)
@WithMockUser(roles = "USER")
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.vault.enabled=false",
    "spring.cloud.consul.enabled=false"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private PatientClient patientClient;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    public void setup() {
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim(any(String.class))).thenReturn(false);
        Mockito.when(jwtDecoder.decode(any(String.class))).thenReturn(jwt);
    }

    @Test
    void testAuthenticate_ValidCredentials() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("username", "password");
        String token = "validToken";
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(token)
            .build();

        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.doNothing().when(valueOperations).set(eq(token), eq(true), eq(Duration.ofSeconds(300)));

        Mockito.when(patientClient.authenticate(any(LoginRequest.class))).thenReturn(tokenResponse);

        // Act & Assert
        ResultActions response = mockMvc.perform(post("/api/authenticate")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.access_token").value(token));

    }

    @Test
    void testAuthenticate_InvalidCredentials() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("username", "wrongPassword");
        Mockito.when(patientClient.authenticate(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/authenticate")
                .header("Authorization", "Bearer invalidToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$").value("Invalid credentials"));
    }

    @Test
    void testAuthenticate_MissingAuthorizationHeader() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("username", "password");

        // Act & Assert
        mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticate_InternalServerError() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("username", "password");
        Mockito.when(patientClient.authenticate(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(post("/api/authenticate")
                .header("Authorization", "Bearer validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());
    }
}
