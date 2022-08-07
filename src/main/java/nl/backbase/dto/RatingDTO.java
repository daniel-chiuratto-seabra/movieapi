package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingDTO implements Serializable {
    @JsonProperty(required = true)
    private String source;
    @JsonProperty(required = true)
    private Double value;
}
