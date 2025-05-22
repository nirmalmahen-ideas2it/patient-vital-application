package com.ideas2it.training.patient.vital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * <p>This class configures the security settings for the application, including
 * endpoint access rules and OAuth2 resource server integration for JWT-based
 * authentication.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * SecurityFilterChain filterChain = new SecurityConfig().filterChain(httpSecurity);
 * </pre>
 *
 * <p>Note: Ensure that the application is properly configured with OAuth2 and JWT
 * settings in the application properties file.</p>
 *
 * @author
 * @version 1.0
 * @since 06/05/2025
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * <p>This method sets up the security rules for the application, including
     * disabling CSRF, permitting access to specific endpoints, and requiring
     * authentication for all other requests. It also configures the application
     * as an OAuth2 resource server with JWT support.</p>
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable);;

        return http.build();
    }
}
