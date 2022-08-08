package nl.backbase.mapper;

import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.RatingDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * This Mapper contains all the mapper methods related to the Rating endpoint flows
 *
 * @author Daniel Chiuratto Seabra
 * @since 04/08/2022
 */
@Component
public class RatingMappers {

    /**
     * This method parses a {@link RatingRequestDTO} into a {@link RatingEntity} to be stored in the database, but it
     * requires to have an {@link Authentication} instance to retrieve the logged username to set in the
     * {@link RatingEntity#setSource(String)} method, and the {@link MovieAPIEntity} instance to set in the
     * {@link RatingEntity#setMovieAPIEntity(MovieAPIEntity)} method. Basically those 2 settings are setting who is rating
     * and with what amount
     *
     * @param ratingRequestDTO {@link RatingRequestDTO} instance containing the user's request to rate the given Movie Title
     * @param authentication {@link Authentication} instance containing the logged username
     * @param movieAPIEntity {@link MovieAPIEntity} to set the Movie which the corresponding {@link RatingEntity} is related with
     *
     * @return {@link RatingEntity} instance ready to be stored into the database (returns {@code null} if one of the parameters are {@code null})
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public RatingEntity ratingRequestDTORatingEntity(final RatingRequestDTO ratingRequestDTO, final Authentication authentication, final MovieAPIEntity movieAPIEntity) {
        if (ratingRequestDTO == null || authentication == null || movieAPIEntity == null) { return null; }
        final var ratingEntity = new RatingEntity();
        ratingEntity.setSource(authentication.getName());
        ratingEntity.setValue(Double.parseDouble(ratingRequestDTO.getValue()));
        ratingEntity.setMovieAPIEntity(movieAPIEntity);
        return ratingEntity;
    }

    /**
     * This method parses a {@link RatingEntity} into a {@link RatingDTO}. This method is used when a {@link BestPictureMovieDTO}
     * is about to be returned to the user with the Oscar's Best Picture movie data, consumed by the
     * {@link MovieMappers#movieAPIEntityToBestPictureMovieDTO(MovieAPIEntity)} parser method
     *
     * @param ratingEntity {@link RatingEntity} instance containing the rating data as {@code Source} and {@code Value}
     * @return {@link RatingDTO} instance with the parsed values (it returns {@code null} where the parameter is {@code null})
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
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

    /**
     * This method parses a {@link Collection<RatingEntity>} into a {@link Collection<RatingDTO>}. This method is used when a {@link BestPictureMovieDTO}
     * is about to be returned to the user with the Oscar's Best Picture movie data, consumed by the
     * {@link MovieMappers#movieAPIEntityToBestPictureMovieDTO(MovieAPIEntity)} parser method
     *
     * @param ratingEntityCollection {@link Collection<RatingEntity>} instance containing the rating data as {@code Source} and {@code Value}
     * @return {@link Collection<RatingDTO>} instance with the parsed values (it returns an empty {@link Collection<RatingDTO>} when the parameter is {@code null})
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public Collection<RatingDTO> ratingEntityRatingDTO(final Collection<RatingEntity> ratingEntityCollection) {
        if (ratingEntityCollection == null) { return Collections.emptyList(); }
        return ratingEntityCollection.stream().map(this::ratingEntityRatingDTO).collect(Collectors.toList());
    }
}
