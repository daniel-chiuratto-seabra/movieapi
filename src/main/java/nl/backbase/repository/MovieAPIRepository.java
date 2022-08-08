package nl.backbase.repository;

import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieTop10Entity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieAPIRepository extends JpaRepository<MovieAPIEntity, Long> {
    MovieAPIEntity findByTitleIgnoreCase(String title);

    @Query(value = "SELECT new nl.backbase.model.MovieTop10Entity(m.title, ROUND(AVG(r.value),2), m.boxOffice, m.oscarWinner) " +
                     "FROM RatingEntity r RIGHT JOIN MovieAPIEntity m " +
                       "ON r.movieAPIEntity.id = m.id " +
                 "GROUP BY m.title " +
                 "ORDER BY ROUND(AVG(r.value),2) DESC, " +
                          "m.boxOffice DESC")
    List<MovieTop10Entity> findTop10OrderedByBoxOffice(Pageable pageable);
}
