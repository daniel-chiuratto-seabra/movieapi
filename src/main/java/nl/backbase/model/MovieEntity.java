package nl.backbase.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * This {@link Entity} represents an actual Movie stored in the database to allow the users to rate it
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@Entity
@Table(name = "MOVIE")
@NoArgsConstructor
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "MOVIE_ID", referencedColumnName = "ID")
    private Collection<RatingEntity> ratings;
    @Column(name = "BOX_OFFICE")
    private BigDecimal boxOffice;
    @Column(name = "BEST_PICTURE_OSCAR_WINNER")
    private Boolean bestPictureOscarWinner;
}
