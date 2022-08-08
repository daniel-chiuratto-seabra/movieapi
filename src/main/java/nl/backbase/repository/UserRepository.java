package nl.backbase.repository;

import nl.backbase.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This {@link Repository} is where the operations related in saving and retrieving user data form the database is made
 * for SignIn/SignUp purposes
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * This method retrieves a {@link UserEntity} from the database by {@link String} username
     *
     * @param username {@link String} representing the requested username
     * @return {@link UserEntity} representing the requested username (or {@code null} if it does not exist)
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    UserEntity findByUsername(String username);
}
