package nl.backbase.repository;

import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieAPISummaryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieAPIRepository extends JpaRepository<MovieAPIEntity, Long> {
    MovieAPIEntity findByTitleIgnoreCase(String title);

    @Query(value = "SELECT new nl.backbase.model.MovieAPISummaryEntity(m.title, ROUND(AVG(r.value),2), m.boxOffice) " +
                     "FROM RatingEntity r RIGHT JOIN MovieAPIEntity m " +
                       "ON m.id = r.movieId " +
                 "GROUP BY m.title " +
                 "ORDER BY m.boxOffice DESC")
    List<MovieAPISummaryEntity> findTop10OrderedByBoxOffice(Pageable pageable);
}
