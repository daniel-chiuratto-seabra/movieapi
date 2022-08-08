package nl.backbase.repository;

import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.model.RatingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.util.List;

/**
 * This {@link Repository} is where all the {@link MovieAPIEntity} are stored, and is where you retrieve the Top 10 list
 * relating the {@link MovieAPIEntity} with the {@link RatingEntity}
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Repository
public interface MovieAPIRepository extends JpaRepository<MovieAPIEntity, Long> {

    /**
     * This method returns an {@link MovieAPIEntity} given a {@link String} presenting the Movie title
     *
     * @param title {@link String} representing the requested Movie title
     * @return {@link MovieAPIEntity} instance related to requested Movie title
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    MovieAPIEntity findByTitleIgnoreCase(String title);

    /**
     * This method returns the Top 10 list of the most rated Movies, where it takes the rate average into account, and
     * in this Top 10, they are sorted by Box Office in descending order. Such query is ran through HQL, where the
     * {@link MovieTop10Entity} is used to represent the {@link List<MovieTop10Entity>} returned from it, not being an
     * actual {@link Entity}
     *
     * @param pageable {@link Pageable} instance used to limit the amount of listed Movies (which in the case needs to be 10)
     * @return {@link List<MovieTop10Entity>} instance containing the set of grouped movies and sorted as described above
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    @Query(value = "SELECT new nl.backbase.model.MovieTop10Entity(m.title, ROUND(AVG(r.value),2), m.boxOffice, m.oscarWinner) " +
                     "FROM RatingEntity r RIGHT JOIN MovieAPIEntity m " +
                       "ON r.movieAPIEntity.id = m.id " +
                 "GROUP BY m.title " +
                 "ORDER BY ROUND(AVG(r.value),2) DESC, " +
                          "m.boxOffice DESC")
    List<MovieTop10Entity> findTop10OrderedByBoxOffice(Pageable pageable);
}
