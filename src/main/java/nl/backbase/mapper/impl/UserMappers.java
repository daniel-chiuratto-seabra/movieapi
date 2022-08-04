package nl.backbase.mapper.impl;

import nl.backbase.dto.UserDTO;
import nl.backbase.model.UserEntity;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMappers {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    public UserEntity userDTOToUserEntity(final UserDTO userDTO) {
        final var userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));
        return userEntity;
    }
}
