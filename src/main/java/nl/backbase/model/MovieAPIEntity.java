package nl.backbase.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Data
@Entity
@Table(name = "MOVIE")
@NoArgsConstructor
@EqualsAndHashCode
public class MovieAPIEntity {
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
}
