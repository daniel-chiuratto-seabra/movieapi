package nl.backbase.mapper.impl;

import nl.backbase.mapper.Mapper;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.model.MovieAPISummaryEntity;
import org.springframework.stereotype.Component;

@Component
public class MovieAPISummaryEntityMovieAPISummaryDTOMapper implements Mapper<MovieAPISummaryEntity, MovieAPISummaryDTO> {
    @Override
    public MovieAPISummaryDTO map(final MovieAPISummaryEntity movieTop10Entity) {
        final var movieTop10DTO = new MovieAPISummaryDTO();
        movieTop10DTO.setTitle(movieTop10Entity.getTitle());

        Double average = movieTop10Entity.getAverage();
        if (average == null) {
            average = 0D;
        }

        movieTop10DTO.setAverage(average);
        movieTop10DTO.setBoxOffice(movieTop10Entity.getBoxOffice());
        return movieTop10DTO;
    }
}
