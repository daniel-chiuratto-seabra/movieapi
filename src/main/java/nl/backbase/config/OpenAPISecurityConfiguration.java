package nl.backbase.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This is the Open API Configuration class. Here is defined that Open API becomes available only when the
 * {@code spring.profiles.active} Spring property is set to {@code dev}, and also configures Open API to request a
 * JWT token set in order to be able to use the API, since Spring Security is configured with it.
 * <br /><br />
 * Each "endpoint/controller class that needs all the endpoints requesting a {@code JWT Token}", needs to add the
 * {@link SecurityRequirement} annotation so Swagger-UI adds the icon where the {@code JWT Token} should be set for
 * authentication. When it is set in the method then it will add the requirement into the endpoint only, and if it is
 * set in the class, it will add the requirement to all the endpoints defined in this class.
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
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenAPISecurityConfiguration {}
