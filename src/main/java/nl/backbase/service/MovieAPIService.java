package nl.backbase.service;

import lombok.RequiredArgsConstructor;
import nl.backbase.csv.CSVData;
import nl.backbase.csv.CSVFileLoaderHelper;
import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieAPISummaryEntity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieAPIRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieAPIService {
    private final MovieAPIRepository movieRepository;
    private final MovieAPISourceService movieSourceService;
    private final Mapper<MovieAPISourceDTO, MovieAPIEntity> movieSourceDTOMovieEntityMapper;
    private final Mapper<MovieAPISummaryEntity, MovieAPISummaryDTO> movieTop10EntityMovieTop10DTOMapper;
    private final Mapper<MovieAPIEntity, MovieAPIDTO> movieEntityMovieDTOMapper;

    public Collection<MovieAPISummaryDTO> getMovieTop10() {
        final Collection<MovieAPISummaryEntity> top10Collection = this.movieRepository.findTop10OrderedByBoxOffice(Pageable.ofSize(10));
        return this.movieTop10EntityMovieTop10DTOMapper.map(top10Collection);
    }

    public void postRating(final RatingRequestDTO ratingRequestDTO) {
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(ratingRequestDTO.getMovieTitle());
        if (movieEntity == null) {
            final var movieSourceDTO = this.movieSourceService.getMovieSourceDTO(ratingRequestDTO.getApiKey(), ratingRequestDTO.getMovieTitle());
            movieEntity = this.movieSourceDTOMovieEntityMapper.map(movieSourceDTO);
        }
        final var ratingEntity = new RatingEntity();
        ratingEntity.setValue(ratingRequestDTO.getValue());

        var ratingsCollection = movieEntity.getRatings();
        if (ratingsCollection == null) {
            ratingsCollection = new ArrayList<>();
            movieEntity.setRatings(ratingsCollection);
        }

        movieEntity.getRatings().add(ratingEntity);
        this.movieRepository.save(movieEntity);
    }

    public Collection<CSVData> saveCSVFile(final String apiKey, final MultipartFile multipartFile) {
        final var csvCollection = CSVFileLoaderHelper.loadFileContent(multipartFile);
        csvCollection.forEach(csvMovie -> {
            var movieEntity = this.movieRepository.findByTitleIgnoreCase(csvMovie.getNominee());
            if (movieEntity == null)
            {
                final var movieSourceDTO = this.movieSourceService.getMovieSourceDTO(apiKey, csvMovie.getNominee(), csvMovie.getAdditionalInfo());
                movieEntity = this.movieSourceDTOMovieEntityMapper.map(movieSourceDTO);
                this.movieRepository.save(movieEntity);
            }
        });
        return csvCollection;
    }

    public MovieAPIDTO getMovie(final String apiKey, final String movieTitle) {
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(movieTitle);
        if (movieEntity == null) {
            final var movieSourceDTO = this.movieSourceService.getMovieSourceDTO(apiKey, movieTitle);
            movieEntity = this.movieSourceDTOMovieEntityMapper.map(movieSourceDTO);
            movieEntity = this.movieRepository.save(movieEntity);
        }
        return this.movieEntityMovieDTOMapper.map(movieEntity);
    }

    // TODO REMOVE THIS METHOD ONCE THE IMPLEMENTATION FINISHES
    public Collection<MovieAPIEntity> getAllMovies() {
        return this.movieRepository.findAll();
    }
}
