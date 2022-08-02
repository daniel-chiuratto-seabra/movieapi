package nl.backbase.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MovieTop10Entity {
    private String title;
    private Double average;
    private BigDecimal boxOffice;
}
