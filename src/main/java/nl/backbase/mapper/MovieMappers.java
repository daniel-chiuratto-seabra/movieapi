package nl.backbase.mapper;

import lombok.RequiredArgsConstructor;
import nl.backbase.controller.MovieAPIRestController;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.repository.MovieAPIRepository;
import nl.backbase.service.MovieAPISourceService;
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

    public static final String OSCAR_WINNER_YES = "YES";
    public static final String OSCAR_WINNER_NO = "NO";

    private final RatingMappers ratingMappers;

    /**
     * This method parses a {@link MovieAPIEntity} instance to {@link BestPictureMovieDTO}, used when
     * {@link MovieAPIRestController#bestpicture(String)} is called to parse the {@link MovieAPIEntity} into a
     * {@link BestPictureMovieDTO} to return to the user
     *
     * @param movieAPIEntity {@link MovieAPIEntity} instance
     * @return {@link BestPictureMovieDTO} instance (if {@code null} then {@code null} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public BestPictureMovieDTO movieAPIEntityToBestPictureMovieDTO(final MovieAPIEntity movieAPIEntity) {
        if (movieAPIEntity == null) { return null; }
        final var movieAPIDTO = new BestPictureMovieDTO();
        movieAPIDTO.setTitle(movieAPIEntity.getTitle());
        movieAPIDTO.setOscarWinner(parseBooleanToOscarWinnerString(movieAPIEntity.getOscarWinner()));
        movieAPIDTO.setRatings(this.ratingMappers.ratingEntityRatingDTO(movieAPIEntity.getRatings()));
        return movieAPIDTO;
    }

    /**
     * This method parses a {@link MovieTop10Entity} into a {@link MovieTop10DTO} instance, used when the
     * {@link MovieAPIRepository#findTop10OrderedByBoxOffice(Pageable)} is called
     *
     * @param movieTop10Entity {@link MovieTop10Entity} instance
     * @return {@link MovieTop10DTO} instance (if {@link MovieTop10Entity} parameter is {@code null} then {@code null} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public MovieTop10DTO movieTop10EntityToMovieTop10DTO(final MovieTop10Entity movieTop10Entity) {
        if (movieTop10Entity == null) { return null; }
        final var movieAPISummaryDTO = new MovieTop10DTO();
        movieAPISummaryDTO.setTitle(movieTop10Entity.getTitle());
        movieAPISummaryDTO.setBoxOffice(movieTop10Entity.getBoxOffice());
        var average = movieTop10Entity.getAverage();
        if (average == null) { average = 0D; }
        movieAPISummaryDTO.setAverage(average);
        movieAPISummaryDTO.setOscarWinner(parseBooleanToOscarWinnerString(movieTop10Entity.getOscarWinner()));
        return movieAPISummaryDTO;
    }

    /**
     * This method parses a {@link MovieAPISourceDTO} into a {@link MovieAPIEntity} instance, used when the application
     * requests Movie Data from the external API Service, to store it in the database, using the {@link RestTemplate} in the
     * {@link MovieAPISourceService#getMovieAPISourceDTO(String, String)} call that returns the {@link MovieAPISourceDTO}
     * instance to be parsed
     *
     * @param movieAPISourceDTO {@link MovieAPISourceDTO} instance
     * @return {@link MovieAPIEntity} instance (if {@code null} then {@code null} is returned)
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    public MovieAPIEntity movieAPISourceDTOToMovieAPIEntity(final MovieAPISourceDTO movieAPISourceDTO) {
        if (movieAPISourceDTO == null) { return null; }
        final var movieAPIEntity = new MovieAPIEntity();
        movieAPIEntity.setTitle(movieAPISourceDTO.getTitle());
        movieAPIEntity.setBoxOffice(ValueParserHelper.getBigDecimalFromString(movieAPISourceDTO.getBoxOffice()));
        movieAPIEntity.setRatings(new ArrayList<>());
        movieAPIEntity.setOscarWinner(false);
        return movieAPIEntity;
    }

    /**
     * This method parses a {@link Collection<MovieTop10Entity>} into a {@link Collection<MovieTop10DTO>} instance, used when the
     * {@link MovieAPIRepository#findTop10OrderedByBoxOffice(Pageable)} is called
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
     * @param oscarWinner {@link Boolean} instance that says if the corresponding Movie won an Oscar or not
     *
     * @return {@link String} representing the values of {@code YES} or {@code NO}, defining if the corresponding Movie
     * won an Oscar
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    private String parseBooleanToOscarWinnerString(final Boolean oscarWinner) {
        return oscarWinner != null && oscarWinner ? OSCAR_WINNER_YES : OSCAR_WINNER_NO;
    }
}
