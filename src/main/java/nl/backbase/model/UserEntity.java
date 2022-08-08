package nl.backbase.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * This class is an {@link Entity} that represents each user that Signed Up in the application and is required for the
 * Sign In, in order to generate the authentication token, carrying the username and password
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@Entity
@Table(name = "USER")
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    public String username;

    @Column(name = "PASSWORD", nullable = false)
    public String password;
}
