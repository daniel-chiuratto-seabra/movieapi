package nl.backbase.mapper.impl;

import nl.backbase.mapper.Mapper;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.model.MovieTop10Entity;
import org.springframework.stereotype.Component;

@Component
public class MovieTop10EntityMovieTop10DTOMapper implements Mapper<MovieTop10Entity, MovieTop10DTO> {
    @Override
    public MovieTop10DTO map(final MovieTop10Entity movieTop10Entity) {
        final var movieTop10DTO = new MovieTop10DTO();
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
