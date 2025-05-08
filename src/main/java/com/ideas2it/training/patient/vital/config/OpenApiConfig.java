package com.ideas2it.training.patient.vital.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation.
 * <p>
 * This class sets up the OpenAPI specification for the application,
 * including security schemes and API metadata.
 * </p>
 *
 * @author Alagu Nirmal Mahendran
 * @created 2025-06-05
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Keycloak";

    @Value("${springdoc.swagger-ui.oauth.authorization-url}")
    private String authorizationUrl;

    @Value("${springdoc.swagger-ui.oauth.token-url}")
    private String tokenUrl;

    /**
     * Configures the custom OpenAPI specification.
     * <p>
     * This method sets up the API title, version, and security requirements
     * using OAuth2 authorization code flow.
     * </p>
     *
     * @return the OpenAPI bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Patient Vital Module").version("v1"))
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                            .authorizationUrl(authorizationUrl)
                            .tokenUrl(tokenUrl)
                            .scopes(new Scopes().addString("openid", "OpenID Connect scope"))
                        )
                    )
            ));
    }

}
