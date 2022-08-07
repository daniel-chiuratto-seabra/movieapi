package nl.backbase.config;

import nl.backbase.dto.UserDTO;
import nl.backbase.service.MovieAPISourceService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * This class is the application main configuration class where needed Beans should be declared. For instance here
 * you have the {@link PasswordEncoder} used for password encoding, as well the {@link RestTemplate} instantiation
 * (this one is being made here in order to allow an easy Mocking of the {@link RestTemplate} instance during the
 * Integration tests)
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Configuration
public class MovieAPIApplicationConfig {
    /**
     * The method is intended in returning an instance of a {@link PasswordEncoder} used during the {@link UserDTO}
     * saving process (the password is saved encoded, naturally)
     *
     * @return {@link PasswordEncoder} instance
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method returns an instance of the {@link RestTemplate}, used by the {@link MovieAPISourceService} to retrieve
     * data from the external Movie API Service
     *
     * @param restTemplateBuilder {@link RestTemplateBuilder} instance, instantiated by Spring automatically
     * @return {@link RestTemplate} instance
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
