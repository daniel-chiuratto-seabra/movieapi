package nl.backbase.service;

import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.helper.csv.CSVData;
import nl.backbase.mapper.impl.MovieMappers;
import nl.backbase.model.MovieAPISummaryEntity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieAPIRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;

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

    public void postRating(final RatingRequestDTO ratingRequestDTO) {
        var movieAPIEntity = this.movieRepository.findByTitleIgnoreCase(ratingRequestDTO.getMovieTitle());
        if (movieAPIEntity == null) {
            final var movieAPISourceDTO = this.movieSourceService.getMovieAPISourceDTO(ratingRequestDTO.getApiKey(), ratingRequestDTO.getMovieTitle());
            movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
        }
        final var ratingEntity = new RatingEntity();
        ratingEntity.setValue(ratingRequestDTO.getValue());

        var ratingsCollection = movieAPIEntity.getRatings();
        if (ratingsCollection == null) {
            ratingsCollection = new ArrayList<>();
            movieAPIEntity.setRatings(ratingsCollection);
        }

        movieAPIEntity.getRatings().add(ratingEntity);
        this.movieRepository.save(movieAPIEntity);
    }

    public Collection<CSVData> saveCSVFile(final MultipartFile multipartFile) {
        final var csvCollection = ValueParserHelper.loadFileContent(multipartFile);
        csvCollection.forEach(csvMovie -> {
            var movieAPIEntity = this.movieRepository.findByTitleIgnoreCase(csvMovie.getNominee());
            if (movieAPIEntity == null)
            {
                final var movieAPISourceDTO = this.movieSourceService.getMovieAPISourceDTO(this.apiKey, csvMovie.getNominee(), csvMovie.getAdditionalInfo());
                movieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(movieAPISourceDTO);
                this.movieRepository.save(movieAPIEntity);
            }
        });
        return csvCollection;
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
}
