package nl.backbase.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.backbase.repository.MovieRepository;
import org.springframework.data.domain.Pageable;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * This class is not an actual {@link Entity}, and yes is a class that represents the grouped result of the query
 * accessible through the {@link MovieRepository#findTop10OrderedByBoxOffice(Pageable)} call, and is what is returned
 * to the user when it requests a Top 10 list, but as a {@link java.util.Collection<MovieTop10Entity>}
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@AllArgsConstructor
public class MovieTop10Entity {
    private String title;
    private Double average;
    private BigDecimal boxOffice;
    private Boolean oscarWinner;
}
