package nl.backbase.service;

import nl.backbase.controller.exception.MovieNotFoundException;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.RatingMappers;
import nl.backbase.model.MovieEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    private MovieService movieService;

    private MovieRepository mockMovieRepository;
    private MovieSourceService mockMovieSourceService;
    private MovieMappers mockMovieMappers;
    private RatingMappers mockRatingMappers;
    private String fakeApiKey;

    @BeforeEach
    private void setUp() {
        this.mockMovieRepository = mock(MovieRepository.class);
        this.mockMovieSourceService = mock(MovieSourceService.class);
        this.mockMovieMappers = mock(MovieMappers.class);
        this.mockRatingMappers = mock(RatingMappers.class);
        this.fakeApiKey = "Fake Api Key";
        this.movieService = new MovieService(mockMovieRepository, mockMovieSourceService, mockMovieMappers, mockRatingMappers, fakeApiKey);
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with mocked dependencies, " +
                  "WHEN getMovieSummaryDTOCollection is called " +
                  "THEN it should return the expected actual MovieTop10DTO collection")
    public void givenMovieSourceServiceWhenGetMovieSummaryDTOCollectionThenItShouldReturnTheExpectedActualMovieSummaryDTOCollection() {
        final var fakePageable = Pageable.ofSize(10);
        final var fakeMovieSummaryEntityList = Collections.<MovieTop10Entity>emptyList();
        final var fakeMovieSummaryDTOList = Collections.<MovieTop10DTO>emptyList();
        when(mockMovieRepository.findTop10OrderedByBoxOffice(eq(fakePageable))).thenReturn(fakeMovieSummaryEntityList);
        when(mockMovieMappers.movieTop10EntityToMovieTop10DTO(eq(fakeMovieSummaryEntityList))).thenReturn(fakeMovieSummaryDTOList);
        final var actualMovieSummaryDTOCollection = movieService.getMovieTop10DTOCollection();
        assertNotNull(actualMovieSummaryDTOCollection);
        verify(mockMovieRepository).findTop10OrderedByBoxOffice(eq(fakePageable));
        verify(mockMovieMappers).movieTop10EntityToMovieTop10DTO(eq(fakeMovieSummaryEntityList));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with mocked dependencies, " +
                  "WHEN savingRatingDTO is called with a fake RatingRequestDTO " +
                   "AND MovieRepository findByTitleIgnoreCase returns null" +
                  "THEN the MovieSourceService getMovieSourceDTO should be called " +
                   "AND the actual RatingRequestDTO response object generated from it")
    public void givenMovieSourceServiceWhenSavingRatingDTOIsCalledThenTheMovieSourceServiceGetMovieSourceDTOShouldBeCalled() {
        final var fakeMovieTitle = "Fake Movie Title";

        final var fakeRatingRequestDTO = new RatingRequestDTO();
        fakeRatingRequestDTO.setMovieTitle(fakeMovieTitle);

        final var fakeMovieSourceDTO = new MovieSourceDTO();

        final var fakeMovieEntity = new MovieEntity();
        fakeMovieEntity.setRatings(new ArrayList<>());

        final var fakeContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(fakeContext);
        final var fakeAuthentication = mock(Authentication.class);
        final var fakeRatingEntity = new RatingEntity();

        when(this.mockMovieSourceService.getMovieSourceDTO(eq(this.fakeApiKey), eq(fakeMovieTitle))).thenReturn(fakeMovieSourceDTO);
        when(this.mockMovieMappers.movieSourceDTOToMovieEntity(eq(fakeMovieSourceDTO))).thenReturn(fakeMovieEntity);
        when(fakeContext.getAuthentication()).thenReturn(fakeAuthentication);
        when(this.mockRatingMappers.ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieEntity))).thenReturn(fakeRatingEntity);

        final var actualRatingRequestDTO = this.movieService.saveRatingRequestDTO(fakeRatingRequestDTO);
        assertNotNull(actualRatingRequestDTO);
        verify(this.mockMovieRepository).findByTitleIgnoreCase(eq(fakeMovieTitle));
        verify(this.mockMovieSourceService).getMovieSourceDTO(eq(this.fakeApiKey), eq(fakeMovieTitle));
        verify(this.mockMovieMappers).movieSourceDTOToMovieEntity(eq(fakeMovieSourceDTO));
        verify(this.mockRatingMappers).ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieEntity));
        verify(this.mockMovieRepository).save(eq(fakeMovieEntity));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with mocked dependencies, " +
                  "WHEN savingRatingDTO is called with a fake RatingRequestDTO " +
                   "AND MovieRepository findByTitleIgnoreCase returns a fake MovieEntity" +
                  "THEN the actual RatingRequestDTO response object should be generated from it")
    public void givenMovieSourceServiceWhenSavingRatingDTOIsCalledThenTheActualRatingRequestDTOResponseObjectShouldBeGeneratedFromIt() {
        final var fakeMovieTitle = "Fake Movie Title";

        final var fakeRatingRequestDTO = new RatingRequestDTO();
        fakeRatingRequestDTO.setMovieTitle(fakeMovieTitle);

        final var fakeMovieSourceDTO = new MovieSourceDTO();

        final var fakeMovieEntity = new MovieEntity();
        fakeMovieEntity.setRatings(new ArrayList<>());

        final var fakeContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(fakeContext);
        final var fakeAuthentication = mock(Authentication.class);
        final var fakeRatingEntity = new RatingEntity();


        when(this.mockMovieRepository.findByTitleIgnoreCase(eq(fakeMovieTitle))).thenReturn(fakeMovieEntity);

        when(fakeContext.getAuthentication()).thenReturn(fakeAuthentication);
        when(this.mockRatingMappers.ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieEntity))).thenReturn(fakeRatingEntity);

        final var actualRatingRequestDTO = this.movieService.saveRatingRequestDTO(fakeRatingRequestDTO);
        assertNotNull(actualRatingRequestDTO);
        verify(this.mockMovieRepository).findByTitleIgnoreCase(eq(fakeMovieTitle));
        verify(this.mockMovieSourceService, never()).getMovieSourceDTO(eq(this.fakeApiKey), eq(fakeMovieTitle));
        verify(this.mockMovieMappers, never()).movieSourceDTOToMovieEntity(eq(fakeMovieSourceDTO));
        verify(this.mockRatingMappers).ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieEntity));
        verify(this.mockMovieRepository).save(eq(fakeMovieEntity));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with mocked dependencies, " +
                  "WHEN savingRatingDTO is called with a fake RatingRequestDTO " +
                   "AND MovieRepository findByTitleIgnoreCase returns null" +
                  "THEN a MovieNotFoundException should be thrown")
    public void givenMovieSourceServiceWhenSavingRatingDTOIsCalledMovieRepositoryReturnsNullThenMovieNotFoundExceptionShouldBeThrown() {
        final var fakeMovieTitle = "Fake Movie Title";
        final var fakeRatingRequestDTO = new RatingRequestDTO();
        fakeRatingRequestDTO.setMovieTitle(fakeMovieTitle);
        assertThrows(MovieNotFoundException.class, () -> this.movieService.saveRatingRequestDTO(fakeRatingRequestDTO), String.format("The searched '%s' movie cannot be found", fakeMovieTitle));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with mocked dependencies, " +
                  "WHEN getBestPictureMovieDTO is called with a fake Movie Title set, " +
                   "AND MovieRepository returns null by calling findByTitleIgnoreCase method " +
                  "THEN it should throw a MovieNotFoundException with the fake Movie Title as message")
    public void givenMovieSourceServiceWhenGetBestPictureMovieDTOCalledMovieRepositoryReturnsNullThenThrowMovieNotFoundException() {
        final var fakeMovieTitle = "Fake Movie Title";
        assertThrows(MovieNotFoundException.class, () -> movieService.getBestPictureMovieDTO(fakeMovieTitle), fakeMovieTitle);
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with mocked dependencies, " +
                  "WHEN getBestPictureMovieDTO is called with a fake Movie Title set, " +
                   "AND MovieRepository returns a fake MovieEntity by calling findByTitleIgnoreCase method " +
                  "THEN it should return the expected actual BestPictureMovieDTO instance")
    public void givenMovieSourceServiceWhenGetBestPictureMovieDTOCalledThenShouldReturnExpectedActualMovieDTOInstance() {
        final var fakeMovieTitle = "Fake Movie Title";
        final var fakeMovieEntity = new MovieEntity();
        final var fakeMovieDTO = new BestPictureMovieDTO();

        when(mockMovieRepository.findByTitleIgnoreCase(eq(fakeMovieTitle))).thenReturn(fakeMovieEntity);
        when(mockMovieMappers.movieEntityToBestPictureMovieDTO(eq(fakeMovieEntity))).thenReturn(fakeMovieDTO);

        final var actualMovieDTo = movieService.getBestPictureMovieDTO(fakeMovieTitle);
        assertNotNull(actualMovieDTo);
        verify(mockMovieRepository).findByTitleIgnoreCase(eq(fakeMovieTitle));
        verify(mockMovieMappers).movieEntityToBestPictureMovieDTO(eq(fakeMovieEntity));
    }
}