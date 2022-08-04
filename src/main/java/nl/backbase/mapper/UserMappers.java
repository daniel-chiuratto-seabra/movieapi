package nl.backbase.mapper;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.UserDTO;
import nl.backbase.model.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMappers {

    private final PasswordEncoder passwordEncoder;

    public UserEntity userDTOToUserEntity(final UserDTO userDTO) {
        if (userDTO == null) { return null; }
        final var userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));
        return userEntity;
    }
}
