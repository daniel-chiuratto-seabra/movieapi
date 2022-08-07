package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserDTO implements Serializable {
    @NotEmpty
    @JsonProperty(required = true)
    private String username;
    @NotEmpty
    @JsonProperty(required = true)
    private String password;
}
