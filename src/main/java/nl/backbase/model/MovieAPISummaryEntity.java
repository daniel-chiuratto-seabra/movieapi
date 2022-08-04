package nl.backbase.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MovieAPISummaryEntity {
    private String title;
    private Double average;
    private BigDecimal boxOffice;
    private Boolean oscarWinner;
}
