package nl.backbase.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieAPISourceDTO implements Serializable {
    @JsonProperty(value = "Title")
    String title;
    @JsonProperty(value = "Ratings")
    Collection<RatingSourceDTO> ratings;
    @JsonProperty(value = "BoxOffice")
    String boxOffice;
    @JsonProperty(value = "Response")
    String response;
}
