package nl.backbase.service;

import lombok.RequiredArgsConstructor;
import nl.backbase.csv.CSVData;
import nl.backbase.csv.CSVFileLoaderHelper;
import nl.backbase.dto.MovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.MovieEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieSourceService movieSourceService;
    private final Mapper<MovieSourceDTO, MovieEntity> movieSourceDTOMovieEntityMapper;
    private final Mapper<MovieTop10Entity, MovieTop10DTO> movieTop10EntityMovieTop10DTOMapper;
    private final Mapper<MovieEntity, MovieDTO> movieEntityMovieDTOMapper;

    public Collection<MovieTop10DTO> getMovieTop10() {
        final Collection<MovieTop10Entity> top10Collection = this.movieRepository.findTop10OrderedByBoxOffice(Pageable.ofSize(10));
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

    public MovieDTO getMovie(final String apiKey, final String movieTitle) {
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(movieTitle);
        if (movieEntity == null) {
            final var movieSourceDTO = this.movieSourceService.getMovieSourceDTO(apiKey, movieTitle);
            movieEntity = this.movieSourceDTOMovieEntityMapper.map(movieSourceDTO);
            movieEntity = this.movieRepository.save(movieEntity);
        }
        return this.movieEntityMovieDTOMapper.map(movieEntity);
    }

    // TODO REMOVE THIS METHOD ONCE THE IMPLEMENTATION FINISHES
    public Collection<MovieEntity> getAllMovies() {
        return this.movieRepository.findAll();
    }
}
