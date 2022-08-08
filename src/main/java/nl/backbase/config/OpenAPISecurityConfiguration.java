package nl.backbase.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This is the Open API Configuration class. Here is defined that Open API becomes available only when the
 * {@code spring.profiles.active} Spring property is set to {@code dev}, and also configures Open API to request a
 * JWT token set in order to be able to use the API, since Spring Security is configured with it.
 * <br /><br />
 * Beyond those definitions, it is important to mention that the application also contains the {@code springdoc-openapi-ui}
 * dependency set in the {@code pom.xml} file that automatically loads {@code Swagger-UI} consuming the Open API generated
 * specs.
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Profile("dev")
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Movie API",
                     version = "v1",
                     contact = @Contact(name = "Daniel Chiuratto Seabra", email = "dseabra84@hotmail.com"),
                     description = "This is an API implemented for a Backbase assessment"),
        servers = @Server(url = "${api.server.url}")
)
public class OpenAPISecurityConfiguration {

    /**
     * This method generates the {@link OpenAPI} instance that configures OpenAPI to generate the API specs for testing
     * purposes, and uses the annotation {@link OpenAPIDefinition} above to configure it
     *
     * @return {@link OpenAPI} instance
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    @Bean
    public OpenAPI customizedOpenAPI() {
        final var securitySchemeName = "Bearer Authentication";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                            new SecurityScheme().name(securitySchemeName)
                                                .description("A JWT Token needs to be provided in order to be able to access the API")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }
}
