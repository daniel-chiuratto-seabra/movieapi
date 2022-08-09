package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * This DTO represents a user rating request for a specific movie title. It accepts a movie title, as well a value between
 * 0 and 10 (with both 0 and 10 included)
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
public class RatingRequestDTO implements Serializable {
    private static final long serialVersionUID = -6389380131081129267L;
	@NotEmpty
    @JsonProperty(value = "movieTitle", required = true)
    private String movieTitle;
    @NotEmpty
    @Pattern(regexp = "\\b(0*[0-9]|10)\\b")
    @JsonProperty(value = "value", required = true)
    private String value;
}
