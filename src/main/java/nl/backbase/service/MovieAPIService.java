package nl.backbase.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.helper.csv.CSVData;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.model.MovieAPISummaryEntity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieAPIRepository;
import org.springframework.beans.factory.annotation.Value;
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

    private final MovieAPIRepository movieRepository;
    private final MovieAPISourceService movieSourceService;
    private final MovieMappers movieMappers;
    private final String apiKey;

    public MovieAPIService(final MovieAPIRepository movieRepository,
                           final MovieAPISourceService movieSourceService,
                           final MovieMappers movieMappers,
                           @Value("${omdbapi.api.key}") final String apiKey) {
        this.movieRepository = movieRepository;
        this.movieSourceService = movieSourceService;
        this.movieMappers = movieMappers;
        this.apiKey = apiKey;
    }

    public Collection<MovieAPISummaryDTO> getMovieTop10() {
        final Collection<MovieAPISummaryEntity> top10Collection = this.movieRepository.findTop10OrderedByBoxOffice(Pageable.ofSize(10));
        return this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO(top10Collection);
    }

    public RatingRequestDTO postRating(final RatingRequestDTO ratingRequestDTO) {
        var movieAPIEntity = this.movieRepository.findByTitleIgnoreCase(ratingRequestDTO.getMovieTitle());
        if (movieAPIEntity == null) {
            final var movieAPISourceDTO = this.movieSourceService.getMovieAPISourceDTO(this.apiKey, ratingRequestDTO.getMovieTitle());
            movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
        }

        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        final var ratingEntity = new RatingEntity();
        ratingEntity.setSource(authentication.getName());
        ratingEntity.setValue(ratingRequestDTO.getValue());
        ratingEntity.setMovieAPIEntity(movieAPIEntity);

        movieAPIEntity.getRatings().add(ratingEntity);
        this.movieRepository.save(movieAPIEntity);

        return ratingRequestDTO;
    }

    public MovieAPIDTO getMovie(final String movieTitle) {
        var movieAPIEntity = this.movieRepository.findByTitleIgnoreCase(movieTitle);
        if (movieAPIEntity == null) {
            final var movieAPISourceDTO = this.movieSourceService.getMovieAPISourceDTO(this.apiKey, movieTitle);
            movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
            movieAPIEntity = this.movieRepository.save(movieAPIEntity);
        }
        return this.movieMappers.movieAPITEntityToMovieAPIDTO(movieAPIEntity);
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
                var movieAPIEntity = this.movieRepository.findByTitleIgnoreCase(csvMovie.getNominee());
                if (movieAPIEntity == null) {
                    final var movieAPISourceDTO = this.movieSourceService.getMovieAPISourceDTOFromCSVFile(this.apiKey, csvMovie.getNominee(), csvMovie.getAdditionalInfo());
                    movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
                    movieAPIEntity.setOscarWinner(true);
                    this.movieRepository.save(movieAPIEntity);
                }
            });
            log.info("FINISHED loading the CSV File Content into the Database...");
        } catch (final Exception e) {
            log.error("An error occurred while loading the file into the database, the process will be skipped", e);
        }
    }
}
