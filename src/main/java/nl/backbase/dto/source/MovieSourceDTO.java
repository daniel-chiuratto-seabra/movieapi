package nl.backbase.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This DTO represents the Movie data returned by the external Movie API Service
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSourceDTO implements Serializable {
    private static final long serialVersionUID = -8102359936075884756L;
	@JsonProperty(value = "Title")
    String title;
    @JsonProperty(value = "BoxOffice")
    String boxOffice;
    @JsonProperty(value = "Response")
    String response;
}
