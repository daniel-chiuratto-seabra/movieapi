package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingRequestDTO implements Serializable {
    @NotEmpty
    @JsonProperty(value = "movieTitle", required = true)
    private String movieTitle;
    @NotEmpty
    @JsonProperty(value = "value", required = true)
    private Double value;
}
