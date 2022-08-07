package nl.backbase.mapper;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieAPISummaryEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMappers {

    private static final String OSCAR_WINNER_YES = "YES";
    private static final String OSCAR_WINNER_NO = "NO";

    private final RatingMappers ratingMappers;

    public BestPictureMovieDTO movieAPIEntityToMovieAPIDTO(final MovieAPIEntity movieAPIEntity) {
        if (movieAPIEntity == null) { return null; }
        final var movieAPIDTO = new BestPictureMovieDTO();
        movieAPIDTO.setTitle(movieAPIEntity.getTitle());
        movieAPIDTO.setOscarWinner(parseBooleanToOscarWinnerString(movieAPIEntity.getOscarWinner()));
        movieAPIDTO.setRatings(this.ratingMappers.ratingEntityRatingDTO(movieAPIEntity.getRatings()));
        return movieAPIDTO;
    }

    public MovieTop10DTO movieAPISummaryEntityToMovieAPISummaryDTO(final MovieAPISummaryEntity movieAPISummaryEntity) {
        if (movieAPISummaryEntity == null) { return null; }
        final var movieAPISummaryDTO = new MovieTop10DTO();
        movieAPISummaryDTO.setTitle(movieAPISummaryEntity.getTitle());
        movieAPISummaryDTO.setBoxOffice(movieAPISummaryEntity.getBoxOffice());
        var average = movieAPISummaryEntity.getAverage();
        if (average == null) { average = 0D; }
        movieAPISummaryDTO.setAverage(average);
        movieAPISummaryDTO.setOscarWinner(parseBooleanToOscarWinnerString(movieAPISummaryEntity.getOscarWinner()));
        return movieAPISummaryDTO;
    }

    public MovieAPIEntity movieAPISourceDTOToMovieAPIEntity(final MovieAPISourceDTO movieAPISourceDTO) {
        if (movieAPISourceDTO == null) { return null; }
        final var movieAPIEntity = new MovieAPIEntity();
        movieAPIEntity.setTitle(movieAPISourceDTO.getTitle());
        movieAPIEntity.setBoxOffice(ValueParserHelper.getBigDecimalFromString(movieAPISourceDTO.getBoxOffice()));
        movieAPIEntity.setRatings(new ArrayList<>());
        movieAPIEntity.setOscarWinner(false);
        return movieAPIEntity;
    }

    public Collection<BestPictureMovieDTO> movieAPIEntityToMovieAPIDTO(final Collection<MovieAPIEntity> movieAPIEntityCollection) {
        if (movieAPIEntityCollection == null) { return Collections.emptyList(); }
        return movieAPIEntityCollection.stream().map(this::movieAPIEntityToMovieAPIDTO).collect(Collectors.toList());
    }

    public Collection<MovieAPIEntity> movieAPISourceDTOToMovieAPIEntity(final Collection<MovieAPISourceDTO> movieAPISourceDTOCollection) {
        if (movieAPISourceDTOCollection == null) { return Collections.emptyList(); }
        return movieAPISourceDTOCollection.stream().map(this::movieAPISourceDTOToMovieAPIEntity).collect(Collectors.toList());
    }

    public Collection<MovieTop10DTO> movieAPISummaryEntityToMovieAPISummaryDTO(final Collection<MovieAPISummaryEntity> movieAPISummaryEntityCollection) {
        if (movieAPISummaryEntityCollection == null) { return Collections.emptyList(); }
        return movieAPISummaryEntityCollection.stream().map(this::movieAPISummaryEntityToMovieAPISummaryDTO).collect(Collectors.toList());
    }

    private String parseBooleanToOscarWinnerString(final Boolean oscarWinner) {
        return oscarWinner != null && oscarWinner ? OSCAR_WINNER_YES : OSCAR_WINNER_NO;
    }
}
