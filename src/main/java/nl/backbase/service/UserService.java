package nl.backbase.service;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.UserDTO;
import nl.backbase.mapper.UserMappers;
import nl.backbase.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMappers userMappers;

    public void saveUserDTO(final UserDTO userDTO) {
        var userEntity = this.userMappers.userDTOToUserEntity(userDTO);
        this.userRepository.save(userEntity);
    }
}
