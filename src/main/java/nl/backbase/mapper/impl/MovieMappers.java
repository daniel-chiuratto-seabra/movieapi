package nl.backbase.mapper.impl;

import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.dto.RatingDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.dto.source.RatingSourceDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieAPISummaryEntity;
import nl.backbase.model.RatingEntity;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Mapper
public abstract class MovieMappers {
    public abstract RatingDTO ratingEntityToRatingDTO(RatingEntity ratingEntity);

    public abstract MovieAPIDTO movieAPITEntityToMovieAPIDTO(MovieAPIEntity movieAPIEntity);

    public abstract MovieAPISummaryDTO movieAPISummaryEntityToMovieAPISummaryDTO(MovieAPISummaryEntity movieAPISummaryEntity);

    public MovieAPIEntity movieAPISourceDTOToMovieAPIEntity(final MovieAPISourceDTO movieAPISourceDTO) {
        final var movieAPIEntity = new MovieAPIEntity();
        movieAPIEntity.setTitle(movieAPISourceDTO.getTitle());
        movieAPIEntity.setBoxOffice(new BigDecimal(movieAPISourceDTO.getBoxOffice().replace("$", "")));
        movieAPIEntity.setRatings(this.ratingSourceDTOToRatingEntity(movieAPISourceDTO.getRatings()));
        return movieAPIEntity;
    }

    public Collection<RatingEntity> ratingSourceDTOToRatingEntity(final Collection<RatingSourceDTO> ratingSourceDTOCollection) {
        if (ratingSourceDTOCollection == null) { return Collections.emptyList(); }
        return ratingSourceDTOCollection.stream().map(this::ratingSourceDTOToRatingEntity).collect(Collectors.toList());
    }

    public RatingEntity ratingSourceDTOToRatingEntity(final RatingSourceDTO ratingSourceDTO) {
        final var ratingEntity = new RatingEntity();
        ratingEntity.setSource(ratingSourceDTO.getSource());
        ratingEntity.setValue(ValueParserHelper.parseValueToDouble(ratingSourceDTO.getValue()));
        return ratingEntity;
    }

    public Collection<MovieAPIDTO> movieAPITEntityToMovieAPIDTO(final Collection<MovieAPIEntity> movieAPIEntityCollection) {
        if (movieAPIEntityCollection == null) { return Collections.emptyList(); }
        return movieAPIEntityCollection.stream().map(this::movieAPITEntityToMovieAPIDTO).collect(Collectors.toList());
    }

    public Collection<MovieAPIEntity> movieAPISourceDTOToMovieAPIEntity(final Collection<MovieAPISourceDTO> movieAPISourceDTOCollection) {
        if (movieAPISourceDTOCollection == null) { return Collections.emptyList(); }
        return movieAPISourceDTOCollection.stream().map(this::movieAPISourceDTOToMovieAPIEntity).collect(Collectors.toList());
    }

    public Collection<MovieAPISummaryDTO> movieAPISummaryEntityToMovieAPISummaryDTO(final Collection<MovieAPISummaryEntity> movieAPISummaryEntityCollection) {
        if (movieAPISummaryEntityCollection == null) { return Collections.emptyList(); }
        return movieAPISummaryEntityCollection.stream().map(this::movieAPISummaryEntityToMovieAPISummaryDTO).collect(Collectors.toList());
    }
}
