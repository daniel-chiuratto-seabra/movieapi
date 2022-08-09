package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This DTO represents the rating element from the rating collection available in the {@link BestPictureMovieDTO}, that shows
 * how many users rated the movie as well their usernames
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
public class RatingDTO implements Serializable {
    private static final long serialVersionUID = -1732529433859637182L;
	@JsonProperty(required = true)
    private String source;
    @JsonProperty(required = true)
    private Double value;
}
