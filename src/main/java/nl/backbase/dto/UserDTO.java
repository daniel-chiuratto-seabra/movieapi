package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * This DTO represents the user data for both SignUp and SignIn operations, being a simple username and password carrier
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 8470356491473338757L;
	@NotEmpty
    @JsonProperty(required = true)
    private String username;
    @NotEmpty
    @JsonProperty(required = true)
    private String password;
}
