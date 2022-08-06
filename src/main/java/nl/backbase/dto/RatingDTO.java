package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingDTO implements Serializable {
    @JsonProperty(required = true)
    private String source;
    @JsonProperty(required = true)
    @Pattern(regexp = "\\b(0*[1-9]|10)\\b")
    private Double value;
}
