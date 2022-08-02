package nl.backbase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class LoginDTO {
    @NotNull
    @Size(min = 1, max = 50)
    private String username;
    @NotNull
    @Size(min = 4, max = 100)
    private String password;
    private Boolean rememberMe;
}
