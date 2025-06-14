package com.ideas2it.training.patient.vital.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @Mock
    private HttpSecurity mockHttpSecurity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig();
    }

    @Test
    void testFilterChain() throws Exception {
        // Arrange
        when(mockHttpSecurity.authorizeHttpRequests(any())).thenReturn(mockHttpSecurity);
        when(mockHttpSecurity.oauth2ResourceServer(any())).thenReturn(mockHttpSecurity);
        when(mockHttpSecurity.sessionManagement(any())).thenReturn(mockHttpSecurity);
        when(mockHttpSecurity.csrf(any())).thenReturn(mockHttpSecurity);

        // Act
        securityConfig.filterChain(mockHttpSecurity);

        // Assert
        verify(mockHttpSecurity).authorizeHttpRequests(any());
        verify(mockHttpSecurity).oauth2ResourceServer(any());
        verify(mockHttpSecurity).sessionManagement(any());
        verify(mockHttpSecurity).csrf(any());
        verify(mockHttpSecurity).build();
    }

}
