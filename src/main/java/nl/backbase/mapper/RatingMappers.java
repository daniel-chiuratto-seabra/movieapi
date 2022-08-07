package nl.backbase.mapper;

import nl.backbase.dto.RatingDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RatingMappers {
    public RatingEntity ratingRequestDTORatingEntity(final RatingRequestDTO ratingRequestDTO, final Authentication authentication, final MovieAPIEntity movieAPIEntity) {
        if (ratingRequestDTO == null || authentication == null || movieAPIEntity == null) { return null; }
        final var ratingEntity = new RatingEntity();
        ratingEntity.setSource(authentication.getName());
        ratingEntity.setValue(Double.parseDouble(ratingRequestDTO.getValue()));
        ratingEntity.setMovieAPIEntity(movieAPIEntity);
        return ratingEntity;
    }

    public RatingDTO ratingEntityRatingDTO(final RatingEntity ratingEntity) {
        if (ratingEntity == null) { return null; }
        final var ratingDTO = new RatingDTO();
        ratingDTO.setSource(ratingEntity.getSource());
        Double value = ratingEntity.getValue();
        if (value == null) {
            value = 0D;
        }
        ratingDTO.setValue(value);
        return ratingDTO;
    }

    public Collection<RatingDTO> ratingEntityRatingDTO(final Collection<RatingEntity> ratingEntityCollection) {
        if (ratingEntityCollection == null) { return Collections.emptyList(); }
        return ratingEntityCollection.stream().map(this::ratingEntityRatingDTO).collect(Collectors.toList());
    }
}
