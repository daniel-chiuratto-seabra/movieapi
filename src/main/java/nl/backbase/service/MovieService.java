package nl.backbase.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieNotFoundException;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.helper.csv.CSVData;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.RatingMappers;
import nl.backbase.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.Executors;

/**
 * This {@link Service} is related in dealing with Movie operations. It is through it that the user can request the
 * available operations such as listing the Top 10 most rated movies,
 */
@Slf4j
@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieSourceService movieSourceService;
    private final MovieMappers movieMappers;
    private final RatingMappers ratingMappers;
    private final String apiKey;

    public MovieService(final MovieRepository movieRepository,
                        final MovieSourceService movieSourceService,
                        final MovieMappers movieMappers,
                        final RatingMappers ratingMappers,
                        @Value("${omdbapi.api.key}") final String apiKey) {
        this.movieRepository = movieRepository;
        this.movieSourceService = movieSourceService;
        this.movieMappers = movieMappers;
        this.ratingMappers = ratingMappers;
        this.apiKey = apiKey;
    }

    public Collection<MovieTop10DTO> getMovieTop10DTOCollection() {
        final var top10Collection = this.movieRepository.findTop10OrderedByBoxOffice(Pageable.ofSize(10));
        return this.movieMappers.movieTop10EntityToMovieTop10DTO(top10Collection);
    }

    public RatingRequestDTO saveRatingRequestDTO(final RatingRequestDTO ratingRequestDTO) {
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(ratingRequestDTO.getMovieTitle());
        if (movieEntity == null) {
            final var movieSourceDTO = this.movieSourceService.getMovieSourceDTO(this.apiKey, ratingRequestDTO.getMovieTitle());
            movieEntity = this.movieMappers.movieSourceDTOToMovieEntity(movieSourceDTO);
        }

        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var ratingEntity = this.ratingMappers.ratingRequestDTORatingEntity(ratingRequestDTO, authentication, movieEntity);
        movieEntity.getRatings().add(ratingEntity);
        this.movieRepository.save(movieEntity);

        ratingRequestDTO.setMovieTitle(movieEntity.getTitle());

        return ratingRequestDTO;
    }

    public BestPictureMovieDTO getBestPictureMovieDTO(final String movieTitle) {
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(movieTitle);
        if (movieEntity == null) {
            throw new MovieNotFoundException(movieTitle);
        }
        return this.movieMappers.movieEntityToBestPictureMovieDTO(movieEntity);
    }


    @Configuration
    @Profile("!test")
    private class CSVFileLoader {
        private final MovieRepository movieRepository;
        private final MovieSourceService movieSourceService;
        private final MovieMappers movieMappers;
        private final String apiKey;

        public CSVFileLoader(final MovieRepository movieRepository,
                             final MovieSourceService movieSourceService,
                             final MovieMappers movieMappers,
                             @Value("${omdbapi.api.key}") final String apiKey) {

            this.movieRepository = movieRepository;
            this.movieSourceService = movieSourceService;
            this.movieMappers = movieMappers;
            this.apiKey = apiKey;
        }

        @PostConstruct
        private void loadMoviesCSVContent() {
            final var csvFileContentInputStream = this.getClass().getClassLoader().getResourceAsStream("academy_awards.csv");
            final var csvCollection = ValueParserHelper.getCSVDataCollectionFromInputStream(csvFileContentInputStream);
            Executors.newSingleThreadExecutor().execute(() -> loadCSVDataIntoDatabase(csvCollection));
        }

        private void loadCSVDataIntoDatabase(final Collection<CSVData> csvCollection) {
            try {
                log.info("LOADING the CSV File Content into the Database...");
                csvCollection.forEach(csvMovie -> {
                    var movieEntity = this.movieRepository.findByTitleIgnoreCase(csvMovie.getNominee());
                    if (movieEntity == null) {
                        final var movieSourceDTO = this.movieSourceService.getMovieSourceDTOFromCSVFile(this.apiKey, csvMovie.getNominee(), csvMovie.getAdditionalInfo());
                        movieEntity = this.movieMappers.movieSourceDTOToMovieEntity(movieSourceDTO);
                        movieEntity.setOscarWinner(true);
                        this.movieRepository.save(movieEntity);
                    }
                });
                log.info("FINISHED loading the CSV File Content into the Database...");
            } catch (final Exception e) {
                log.error("An error occurred while loading the file into the database, the process will be skipped", e);
            }
        }
    }
}
