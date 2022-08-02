package nl.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MovieTop10DTO implements Serializable {
    @JsonProperty
    private String title;
    @JsonProperty
    private Double average;
    @JsonProperty
    private BigDecimal boxOffice;
}
