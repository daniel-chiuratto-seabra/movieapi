package nl.backbase.mapper.impl;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.UserDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDTOUserEntityMapper implements Mapper<UserDTO, UserEntity> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserEntity map(final UserDTO userDTO) {
        final var userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(this.bCryptPasswordEncoder.encode(userDTO.getPassword()));
        return userEntity;
    }
}
