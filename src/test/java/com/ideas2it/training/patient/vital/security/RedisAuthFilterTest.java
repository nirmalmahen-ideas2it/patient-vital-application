package com.ideas2it.training.patient.vital.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class RedisAuthFilterTest {

    private RedisAuthFilter redisAuthFilter;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisAuthFilter = new RedisAuthFilter(redisTemplate);
    }

    @Test
    void testDoFilterInternalWithMissingAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        PrintWriter writer = mock(PrintWriter.class);
        when(request.getRequestURI()).thenReturn("/api/vital-signs");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        // Act
        redisAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).getWriter();
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithInvalidAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        PrintWriter writer = mock(PrintWriter.class);
        when(request.getRequestURI()).thenReturn("/api/vital-signs");
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");
        when(response.getWriter()).thenReturn(writer);

        // Act
        redisAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).getWriter();
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithExpiredToken() throws ServletException, IOException {
        // Arrange
        PrintWriter writer = mock(PrintWriter.class);
        when(request.getRequestURI()).thenReturn("/api/vital-signs");
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(redisTemplate.hasKey("expiredToken")).thenReturn(false);
        when(response.getWriter()).thenReturn(writer);

        // Act
        redisAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).getWriter();
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/patients");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(redisTemplate.hasKey("validToken")).thenReturn(true);

        // Act
        redisAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalForNonProtectedPath() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        redisAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
