package nl.backbase.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieAPINotFoundException;
import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.helper.csv.CSVData;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.RatingMappers;
import nl.backbase.repository.MovieAPIRepository;
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

@Slf4j
@Service
@Transactional
public class MovieAPIService {

    private final MovieAPIRepository movieAPIRepository;
    private final MovieAPISourceService movieAPISourceService;
    private final MovieMappers movieMappers;
    private final RatingMappers ratingMappers;
    private final String apiKey;

    public MovieAPIService(final MovieAPIRepository movieRepository,
                           final MovieAPISourceService movieSourceService,
                           final MovieMappers movieMappers,
                           final RatingMappers ratingMappers,
                           @Value("${omdbapi.api.key}") final String apiKey) {
        this.movieAPIRepository = movieRepository;
        this.movieAPISourceService = movieSourceService;
        this.movieMappers = movieMappers;
        this.ratingMappers = ratingMappers;
        this.apiKey = apiKey;
    }

    public Collection<MovieAPISummaryDTO> getMovieAPISummaryDTOCollection() {
        final var top10Collection = this.movieAPIRepository.findTop10OrderedByBoxOffice(Pageable.ofSize(10));
        return this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO(top10Collection);
    }

    public RatingRequestDTO saveRatingDTO(final RatingRequestDTO ratingRequestDTO) {
        var movieAPIEntity = this.movieAPIRepository.findByTitleIgnoreCase(ratingRequestDTO.getMovieTitle());
        if (movieAPIEntity == null) {
            final var movieAPISourceDTO = this.movieAPISourceService.getMovieAPISourceDTO(this.apiKey, ratingRequestDTO.getMovieTitle());
            movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
        }

        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var ratingEntity = this.ratingMappers.ratingRequestDTORatingEntity(ratingRequestDTO, authentication, movieAPIEntity);
        movieAPIEntity.getRatings().add(ratingEntity);
        this.movieAPIRepository.save(movieAPIEntity);

        return ratingRequestDTO;
    }

    public MovieAPIDTO getBestPictureMovieAPIDTO(final String movieTitle) {
        var movieAPIEntity = this.movieAPIRepository.findByTitleIgnoreCase(movieTitle);
        if (movieAPIEntity == null) {
            throw new MovieAPINotFoundException(movieTitle);
        }
        return this.movieMappers.movieAPIEntityToMovieAPIDTO(movieAPIEntity);
    }


    @Configuration
    @Profile("!test")
    private class CSVFileLoader {
        private final MovieAPIRepository movieAPIRepository;
        private final MovieAPISourceService movieAPISourceService;
        private final MovieMappers movieMappers;
        private final String apiKey;

        public CSVFileLoader(final MovieAPIRepository movieAPIRepository,
                             final MovieAPISourceService movieAPISourceService,
                             final MovieMappers movieMappers,
                             @Value("${omdbapi.api.key}") final String apiKey) {

            this.movieAPIRepository = movieAPIRepository;
            this.movieAPISourceService = movieAPISourceService;
            this.movieMappers = movieMappers;
            this.apiKey = apiKey;
        }

        @PostConstruct
        private void loadMoviesCSVContent() {
            final var csvFileContentInputStream = this.getClass().getClassLoader().getResourceAsStream("academy_awards.csv");
            final var csvCollection = ValueParserHelper.loadFileContent(csvFileContentInputStream);
            Executors.newSingleThreadExecutor().execute(() -> loadCSVDataIntoDatabase(csvCollection));
        }

        private void loadCSVDataIntoDatabase(final Collection<CSVData> csvCollection) {
            try {
                log.info("LOADING the CSV File Content into the Database...");
                csvCollection.forEach(csvMovie -> {
                    var movieAPIEntity = this.movieAPIRepository.findByTitleIgnoreCase(csvMovie.getNominee());
                    if (movieAPIEntity == null) {
                        final var movieAPISourceDTO = this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile(this.apiKey, csvMovie.getNominee(), csvMovie.getAdditionalInfo());
                        movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
                        movieAPIEntity.setOscarWinner(true);
                        this.movieAPIRepository.save(movieAPIEntity);
                    }
                });
                log.info("FINISHED loading the CSV File Content into the Database...");
            } catch (final Exception e) {
                log.error("An error occurred while loading the file into the database, the process will be skipped", e);
            }
        }
    }
}
