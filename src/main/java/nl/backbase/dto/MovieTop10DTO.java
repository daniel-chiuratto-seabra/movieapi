package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This DTO represents the collection elements of the Top 10 list
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
public class MovieTop10DTO implements Serializable {
    @JsonProperty
    private String title;
    @JsonProperty
    private Double average;
    @JsonProperty
    private BigDecimal boxOffice;
    @JsonProperty
    private String oscarWinner;
}
