package nl.backbase.repository;

import nl.backbase.model.MovieEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.model.RatingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.util.List;

/**
 * This {@link Repository} is where all the {@link MovieEntity} are stored, and is where you retrieve the Top 10 list
 * relating the {@link MovieEntity} with the {@link RatingEntity}
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    /**
     * This method returns an {@link MovieEntity} given a {@link String} presenting the Movie title
     *
     * @param title {@link String} representing the requested Movie title
     * @return {@link MovieEntity} instance related to requested Movie title
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    MovieEntity findByTitleIgnoreCase(String title);

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
    @Query(value = "SELECT new nl.backbase.model.MovieTop10Entity(m.title, ROUND(AVG(r.value),2), m.boxOffice, m.bestPictureOscarWinner) " +
                     "FROM RatingEntity r RIGHT JOIN MovieEntity m " +
                       "ON r.movieEntity.id = m.id " +
                 "GROUP BY m.title " +
                 "ORDER BY ROUND(AVG(r.value),2) DESC, " +
                          "m.boxOffice DESC")
    List<MovieTop10Entity> findTop10OrderedByBoxOffice(Pageable pageable);
}
