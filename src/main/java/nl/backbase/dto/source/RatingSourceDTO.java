package nl.backbase.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingSourceDTO {
    @JsonProperty("Source")
    private String source;
    @JsonProperty("Value")
    private String value;
}
