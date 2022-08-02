package nl.backbase.service;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.UserDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mapper<UserDTO, UserEntity> userDTOUserEntityMapper;

    public void saveUserDTO(final UserDTO userDTO) {
        var userEntity = this.userDTOUserEntityMapper.map(userDTO);
        this.userRepository.save(userEntity);
    }
}
