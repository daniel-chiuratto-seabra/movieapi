package nl.backbase.mapper.impl;

import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.RatingDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class MovieAPIEntityMovieDTOMapper implements Mapper<MovieAPIEntity, MovieAPIDTO> {
    @Override
    public MovieAPIDTO map(final MovieAPIEntity movieEntity) {
        final var movieDTO = new MovieAPIDTO();
        movieDTO.setTitle(movieEntity.getTitle());
        movieDTO.setRatings(parseRatingEntityCollectionToRatingDTOCollection(movieEntity.getRatings()));
        return movieDTO;
    }

    private Collection<RatingDTO> parseRatingEntityCollectionToRatingDTOCollection(final Collection<RatingEntity> ratingEntityCollection) {
        final var ratingDTOCollection = new ArrayList<RatingDTO>();
        if (CollectionUtils.isNotEmpty(ratingEntityCollection)) {
            ratingEntityCollection.forEach(ratingEntity -> ratingDTOCollection.add(parseRatingEntityToRatingDTO(ratingEntity)));
        }
        return ratingDTOCollection;
    }

    private RatingDTO parseRatingEntityToRatingDTO(final RatingEntity ratingEntity) {
        final var ratingDTO = new RatingDTO();
        ratingDTO.setSource(ratingEntity.getSource());
        ratingDTO.setValue(ratingEntity.getValue());
        return ratingDTO;
    }
}
