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

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Movie API",
                     version = "v1",
                     contact = @Contact(name = "Daniel Chiuratto Seabra", email = "dseabra84@hotmail.com"),
                     description = "This is an API implemented for a Backbase assessment"),
        servers = @Server(url = "${api.server.url}")
)
public class OpenAPISecurityConfiguration {
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
