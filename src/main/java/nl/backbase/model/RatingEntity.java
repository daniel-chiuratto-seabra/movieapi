package nl.backbase.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RATING")
@NoArgsConstructor
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SOURCE", nullable = false)
    private String source;
    @Column(name = "VALUE", nullable = false)
    private Double value;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "MOVIE_ID", referencedColumnName = "ID")
    private MovieAPIEntity movieAPIEntity;
}
