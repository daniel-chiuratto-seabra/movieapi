package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingRequestDTO implements Serializable {
    @NotEmpty
    @JsonProperty(value = "movieTitle", required = true)
    private String movieTitle;
    @NotEmpty
    @Pattern(regexp = "\\b(0*[0-9]|10)\\b")
    @JsonProperty(value = "value", required = true)
    private String value;
}
