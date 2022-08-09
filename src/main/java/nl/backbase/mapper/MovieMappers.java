package nl.backbase.mapper;

import lombok.RequiredArgsConstructor;
import nl.backbase.controller.MovieRestController;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.model.MovieEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.repository.MovieRepository;
import nl.backbase.service.MovieSourceService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * This Mapper contains all the mapper methods related to the Movie endpoint flows
 *
 * @author Daniel Chiuratto Seabra
 * @since 04/08/2022
 */
@Component
@RequiredArgsConstructor
public class MovieMappers {

    public static final String BEST_PICTURE_OSCAR_WINNER_YES = "YES";
    public static final String BEST_PICTURE_OSCAR_WINNER_NO = "NO";

    private final RatingMappers ratingMappers;

    /**
     * This method parses a {@link MovieEntity} instance to {@link BestPictureMovieDTO}, used when
     * {@link MovieRestController#bestpicture(String)} is called to parse the {@link MovieEntity} into a
     * {@link BestPictureMovieDTO} to return to the user
     *
     * @param movieEntity {@link MovieEntity} instance
     * @return {@link BestPictureMovieDTO} instance (if {@code null} then {@code null} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public BestPictureMovieDTO movieEntityToBestPictureMovieDTO(final MovieEntity movieEntity) {
        if (movieEntity == null) { return null; }
        final var bestPictureMovieDTO = new BestPictureMovieDTO();
        bestPictureMovieDTO.setTitle(movieEntity.getTitle());
        bestPictureMovieDTO.setBestPictureOscarWinner(parseBooleanToBestPictureOscarWinnerString(movieEntity.getBestPictureOscarWinner()));
        bestPictureMovieDTO.setRatings(this.ratingMappers.ratingEntityRatingDTO(movieEntity.getRatings()));
        return bestPictureMovieDTO;
    }

    /**
     * This method parses a {@link MovieTop10Entity} into a {@link MovieTop10DTO} instance, used when the
     * {@link MovieRepository#findTop10OrderedByBoxOffice(Pageable)} is called
     *
     * @param movieTop10Entity {@link MovieTop10Entity} instance
     * @return {@link MovieTop10DTO} instance (if {@link MovieTop10Entity} parameter is {@code null} then {@code null} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public MovieTop10DTO movieTop10EntityToMovieTop10DTO(final MovieTop10Entity movieTop10Entity) {
        if (movieTop10Entity == null) { return null; }
        final var movieTop10DTO = new MovieTop10DTO();
        movieTop10DTO.setTitle(movieTop10Entity.getTitle());
        movieTop10DTO.setBoxOffice(movieTop10Entity.getBoxOffice());
        var average = movieTop10Entity.getAverage();
        if (average == null) { average = 0D; }
        movieTop10DTO.setAverage(average);
        movieTop10DTO.setBestPictureOscarWinner(parseBooleanToBestPictureOscarWinnerString(movieTop10Entity.getBestPictureOscarWinner()));
        return movieTop10DTO;
    }

    /**
     * This method parses a {@link MovieSourceDTO} into a {@link MovieEntity} instance, used when the application
     * requests Movie Data from the external API Service, to store it in the database, using the {@link RestTemplate} in the
     * {@link MovieSourceService#getMovieSourceDTO(String, String)} call that returns the {@link MovieSourceDTO}
     * instance to be parsed
     *
     * @param movieSourceDTO {@link MovieSourceDTO} instance
     * @return {@link MovieEntity} instance (if {@code null} then {@code null} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public MovieEntity movieSourceDTOToMovieEntity(final MovieSourceDTO movieSourceDTO) {
        if (movieSourceDTO == null) { return null; }
        final var movieEntity = new MovieEntity();
        movieEntity.setTitle(movieSourceDTO.getTitle());
        movieEntity.setBoxOffice(ValueParserHelper.getBigDecimalFromString(movieSourceDTO.getBoxOffice()));
        movieEntity.setRatings(new ArrayList<>());
        movieEntity.setBestPictureOscarWinner(false);
        return movieEntity;
    }

    /**
     * This method parses a {@link Collection<MovieTop10Entity>} into a {@link Collection<MovieTop10DTO>} instance, used when the
     * {@link MovieRepository#findTop10OrderedByBoxOffice(Pageable)} is called
     *
     * @param movieTop10EntityCollection {@link Collection<MovieTop10Entity>} instance
     * @return {@link Collection<MovieTop10DTO>} instance (if {@link Collection<MovieTop10Entity>} parameter is {@code null}
     * then an {@code empty} {@link Collection} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public Collection<MovieTop10DTO> movieTop10EntityToMovieTop10DTO(final Collection<MovieTop10Entity> movieTop10EntityCollection) {
        if (movieTop10EntityCollection == null) { return Collections.emptyList(); }
        return movieTop10EntityCollection.stream().map(this::movieTop10EntityToMovieTop10DTO).collect(Collectors.toList());
    }

    /**
     * This method receives the {@link Boolean} from the external Movie API Source, and converts into the CSV File format,
     * where instead of having {@link Boolean#TRUE} or {@link Boolean#FALSE}, you may have {@link String} {@code YES} or
     * {@code NO}
     *
     * @param bestPictureOscarWinner {@link Boolean} instance that says if the corresponding Movie won an Oscar or not
     *
     * @return {@link String} representing the values of {@code YES} or {@code NO}, defining if the corresponding Movie
     * won an Oscar
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    private String parseBooleanToBestPictureOscarWinnerString(final Boolean bestPictureOscarWinner) {
        return bestPictureOscarWinner != null && bestPictureOscarWinner ? BEST_PICTURE_OSCAR_WINNER_YES : BEST_PICTURE_OSCAR_WINNER_NO;
    }
}
