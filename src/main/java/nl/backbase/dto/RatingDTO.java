package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingDTO implements Serializable {
    @JsonProperty
    private String source;
    @JsonProperty
    private Double value;
}
