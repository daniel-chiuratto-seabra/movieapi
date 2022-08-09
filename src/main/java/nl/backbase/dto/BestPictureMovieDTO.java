package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

/**
 * This DTO represents the Oscar Best Picture movie returned by the API when the user requests it, showing also its
 * ratings
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
public class BestPictureMovieDTO implements Serializable {
    private static final long serialVersionUID = 8790702632274836870L;
	@JsonProperty
    private String title;
    @JsonProperty
    private Collection<RatingDTO> ratings;
    @JsonProperty
    private String bestPictureOscarWinner;
}
