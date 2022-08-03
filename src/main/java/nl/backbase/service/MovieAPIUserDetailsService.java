package nl.backbase.service;

import lombok.RequiredArgsConstructor;
import nl.backbase.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieAPIUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final var userEntity = this.userRepository.findByUsername(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }

        return User.withUsername(username)
                .password(userEntity.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
