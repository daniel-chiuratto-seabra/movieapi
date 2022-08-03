package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
public class MovieAPIDTO implements Serializable {
    @JsonProperty
    private String title;
    @JsonProperty
    private Collection<RatingDTO> ratings;
}
