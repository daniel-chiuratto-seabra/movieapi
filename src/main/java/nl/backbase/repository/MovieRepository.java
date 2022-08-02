package nl.backbase.repository;

import nl.backbase.model.MovieEntity;
import nl.backbase.model.MovieTop10Entity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    MovieEntity findByTitleIgnoreCase(String title);

    @Query(value = "SELECT new nl.backbase.model.MovieTop10Entity(m.title, ROUND(AVG(r.value),2), m.boxOffice) " +
                     "FROM MovieEntity m LEFT JOIN RatingEntity r " +
                       "ON m.id = r.movieId " +
                 "GROUP BY m.title " +
                 "ORDER BY m.boxOffice DESC")
    List<MovieTop10Entity> findTop10OrderedByBoxOffice(Pageable pageable);
}
