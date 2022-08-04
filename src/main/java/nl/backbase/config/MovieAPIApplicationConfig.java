package nl.backbase.config;

import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.UserMappers;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MovieAPIApplicationConfig {
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MovieMappers movieAPIMapper() {
        return Mappers.getMapper(MovieMappers.class);
    }

    @Bean
    public UserMappers userMappers() {
        return Mappers.getMapper(UserMappers.class);
    }
}
