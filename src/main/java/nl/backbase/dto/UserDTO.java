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
    @JsonProperty
    private String username;
    @NotEmpty
    @JsonProperty
    private String password;
    @NotEmpty
    @JsonProperty
    private String apiKey;
}
