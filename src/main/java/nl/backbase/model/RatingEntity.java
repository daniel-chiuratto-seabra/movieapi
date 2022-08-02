package nl.backbase.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RATING")
@NoArgsConstructor
@EqualsAndHashCode
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SOURCE", nullable = false)
    private String source;
    @Column(name = "VALUE", nullable = false)
    private Double value;
    @Column(name = "MOVIE_ID")
    private Long movieId;
}
