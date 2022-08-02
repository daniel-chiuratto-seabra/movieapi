package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingRequestDTO implements Serializable {
    @JsonProperty("apiKey")
    private String apiKey;
    @JsonProperty("movieTitle")
    private String movieTitle;
    @JsonProperty("value")
    private Double value;
}
