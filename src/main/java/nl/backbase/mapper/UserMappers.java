package nl.backbase.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.dto.UserDTO;
import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;
import nl.backbase.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This Mapper contains all the mapper methods related to the SignIn/SignOut endpoint flows
 *
 * @author Daniel Chiuratto Seabra
 * @since 04/08/2022
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserMappers {

    private final PasswordEncoder passwordEncoder;

    /**
     * This method parses a {@link UserDTO} into a {@link UserEntity} in order to save the username and password into
     * the database, encoding the password before saving it with a {@link PasswordEncoder} instance.
     * It is consumed by the {@link UserService#saveUserDTO(UserDTO)}
     *
     * @param userDTO {@link UserDTO} instance
     * @return {@link UserEntity} instance ready to be saved in the {@link UserRepository} (it returns {@code null} if
     * the parameter is also {@code null})
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public UserEntity userDTOToUserEntity(final UserDTO userDTO) {
        if (userDTO == null) { return null; }
        log.debug("Parsing the UserDTO entity: {}", userDTO);
        final var userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));
        log.trace("Returning the parsed UserEntity: {}", userEntity);
        return userEntity;
    }
}
