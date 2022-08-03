package nl.backbase.mapper.impl;

import nl.backbase.csv.ValueParserHelper;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.dto.source.RatingSourceDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MovieAPISourceDTOMovieEntityMapper implements Mapper<MovieAPISourceDTO, MovieAPIEntity> {
    @Override
    public MovieAPIEntity map(final MovieAPISourceDTO movieSourceDTO) {
        if (movieSourceDTO == null) {
            return null;
        }

        final var movieEntity = new MovieAPIEntity();
        movieEntity.setBoxOffice(ValueParserHelper.getBigDecimalFromString(movieSourceDTO.getBoxOffice()));
        movieEntity.setTitle(movieSourceDTO.getTitle());

        final var ratingRequests = movieSourceDTO.getRatings();
        if (CollectionUtils.isNotEmpty(ratingRequests)) {
            final var ratingEntityCollection = new ArrayList<RatingEntity>();
            ratingRequests.forEach(ratingDTO -> ratingEntityCollection.add(parseRatingDTOToRatingEntity(ratingDTO)));
            movieEntity.setRatings(ratingEntityCollection);
        }

        return movieEntity;
    }

    private RatingEntity parseRatingDTOToRatingEntity(final RatingSourceDTO ratingDTO) {
        final var ratingEntity = new RatingEntity();
        ratingEntity.setSource(ratingDTO.getSource());
        ratingEntity.setValue(ValueParserHelper.parseValueToDouble(ratingDTO.getValue()));
        return ratingEntity;
    }
}
