package nl.backbase.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.UserAlreadyExistException;
import nl.backbase.dto.UserDTO;
import nl.backbase.mapper.UserMappers;
import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * This {@link Service} is related in dealing with User operations. Since the operation of loading the {@link UserEntity}
 * instance from the database is made by one of the Spring filter implementations, only one operation is found in this
 * class
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMappers userMappers;

    /**
     * This method saves the informed {@link UserDTO} into the {@link UserRepository}
     *
     * @param userDTO {@link UserDTO} instance with the credentials ready to be saved in the {@link UserRepository}
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    public void saveUserDTO(final UserDTO userDTO) {
        // Before saving the User, it is verified if it already exists
        log.debug("Searching user '{}' in the database", userDTO.getUsername());
        var userEntity = this.userRepository.findByUsername(userDTO.getUsername());
        if (userEntity != null) {
            // If the User already exists, then a UserAlreadyExistException is thrown, where this flow
            // attends the scenario of a user trying to SignUp with a username that already exists in the system
            throw new UserAlreadyExistException(String.format("The user '%s' already exists", userEntity.getUsername()));
        }
        // If the User does not exist in the system yet, then it is parsed into
        // a UserEntity
        log.debug("User '{}' not found in the database, creating a new one and saving into the database", userDTO.getUsername());
        userEntity = this.userMappers.userDTOToUserEntity(userDTO);
        // Then it is saved into the repository
        this.userRepository.save(userEntity);
    }
}
