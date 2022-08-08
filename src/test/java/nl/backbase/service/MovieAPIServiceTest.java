package nl.backbase.service;

import nl.backbase.controller.exception.MovieAPINotFoundException;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.RatingMappers;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieAPIRepository;
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

class MovieAPIServiceTest {

    private MovieAPIService movieAPISourceService;

    private MovieAPIRepository mockMovieAPIRepository;
    private MovieAPISourceService mockMovieAPISourceService;
    private MovieMappers mockMovieMappers;
    private RatingMappers mockRatingMappers;
    private String fakeApiKey;

    @BeforeEach
    private void setUp() {
        this.mockMovieAPIRepository = mock(MovieAPIRepository.class);
        this.mockMovieAPISourceService = mock(MovieAPISourceService.class);
        this.mockMovieMappers = mock(MovieMappers.class);
        this.mockRatingMappers = mock(RatingMappers.class);
        this.fakeApiKey = "Fake Api Key";
        this.movieAPISourceService = new MovieAPIService(mockMovieAPIRepository, mockMovieAPISourceService, mockMovieMappers, mockRatingMappers, fakeApiKey);
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with mocked dependencies, " +
                  "WHEN getMovieAPISummaryDTOCollection is called " +
                  "THEN it should return the expected actual MovieTop10DTO collection")
    public void givenMovieAPISourceServiceWhenGetMovieAPISummaryDTOCollectionThenItShouldReturnTheExpectedActualMovieAPISummaryDTOCollection() {
        final var fakePageable = Pageable.ofSize(10);
        final var fakeMovieAPISummaryEntityList = Collections.<MovieTop10Entity>emptyList();
        final var fakeMovieAPISummaryDTOList = Collections.<MovieTop10DTO>emptyList();
        when(mockMovieAPIRepository.findTop10OrderedByBoxOffice(eq(fakePageable))).thenReturn(fakeMovieAPISummaryEntityList);
        when(mockMovieMappers.movieTop10EntityToMovieTop10DTO(eq(fakeMovieAPISummaryEntityList))).thenReturn(fakeMovieAPISummaryDTOList);
        final var actualMovieAPISummaryDTOCollection = movieAPISourceService.getMovieAPISummaryDTOCollection();
        assertNotNull(actualMovieAPISummaryDTOCollection);
        verify(mockMovieAPIRepository).findTop10OrderedByBoxOffice(eq(fakePageable));
        verify(mockMovieMappers).movieTop10EntityToMovieTop10DTO(eq(fakeMovieAPISummaryEntityList));
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with mocked dependencies, " +
                  "WHEN savingRatingDTO is called with a fake RatingRequestDTO " +
                   "AND MovieAPIRepository findByTitleIgnoreCase returns null" +
                  "THEN the MovieAPISourceService getMovieAPISourceDTO should be called " +
                   "AND the actual RatingRequestDTO response object generated from it")
    public void givenMovieAPISourceServiceWhenSavingRatingDTOIsCalledThenTheMovieAPISourceServiceGetMovieAPISourceDTOShouldBeCalled() {
        final var fakeMovieTitle = "Fake Movie Title";

        final var fakeRatingRequestDTO = new RatingRequestDTO();
        fakeRatingRequestDTO.setMovieTitle(fakeMovieTitle);

        final var fakeMovieAPISourceDTO = new MovieAPISourceDTO();

        final var fakeMovieAPIEntity = new MovieAPIEntity();
        fakeMovieAPIEntity.setRatings(new ArrayList<>());

        final var fakeContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(fakeContext);
        final var fakeAuthentication = mock(Authentication.class);
        final var fakeRatingEntity = new RatingEntity();

        when(this.mockMovieAPISourceService.getMovieAPISourceDTO(eq(this.fakeApiKey), eq(fakeMovieTitle))).thenReturn(fakeMovieAPISourceDTO);
        when(this.mockMovieMappers.movieAPISourceDTOToMovieAPIEntity(eq(fakeMovieAPISourceDTO))).thenReturn(fakeMovieAPIEntity);
        when(fakeContext.getAuthentication()).thenReturn(fakeAuthentication);
        when(this.mockRatingMappers.ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieAPIEntity))).thenReturn(fakeRatingEntity);

        final var actualRatingRequestDTO = this.movieAPISourceService.saveRatingDTO(fakeRatingRequestDTO);
        assertNotNull(actualRatingRequestDTO);
        verify(this.mockMovieAPIRepository).findByTitleIgnoreCase(eq(fakeMovieTitle));
        verify(this.mockMovieAPISourceService).getMovieAPISourceDTO(eq(this.fakeApiKey), eq(fakeMovieTitle));
        verify(this.mockMovieMappers).movieAPISourceDTOToMovieAPIEntity(eq(fakeMovieAPISourceDTO));
        verify(this.mockRatingMappers).ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieAPIEntity));
        verify(this.mockMovieAPIRepository).save(eq(fakeMovieAPIEntity));
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with mocked dependencies, " +
                  "WHEN savingRatingDTO is called with a fake RatingRequestDTO " +
                   "AND MovieAPIRepository findByTitleIgnoreCase returns a fake MovieAPIEntity" +
                  "THEN the actual RatingRequestDTO response object should be generated from it")
    public void givenMovieAPISourceServiceWhenSavingRatingDTOIsCalledThenTheActualRatingRequestDTOResponseObjectShouldBeGeneratedFromIt() {
        final var fakeMovieTitle = "Fake Movie Title";

        final var fakeRatingRequestDTO = new RatingRequestDTO();
        fakeRatingRequestDTO.setMovieTitle(fakeMovieTitle);

        final var fakeMovieAPISourceDTO = new MovieAPISourceDTO();

        final var fakeMovieAPIEntity = new MovieAPIEntity();
        fakeMovieAPIEntity.setRatings(new ArrayList<>());

        final var fakeContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(fakeContext);
        final var fakeAuthentication = mock(Authentication.class);
        final var fakeRatingEntity = new RatingEntity();


        when(this.mockMovieAPIRepository.findByTitleIgnoreCase(eq(fakeMovieTitle))).thenReturn(fakeMovieAPIEntity);

        when(fakeContext.getAuthentication()).thenReturn(fakeAuthentication);
        when(this.mockRatingMappers.ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieAPIEntity))).thenReturn(fakeRatingEntity);

        final var actualRatingRequestDTO = this.movieAPISourceService.saveRatingDTO(fakeRatingRequestDTO);
        assertNotNull(actualRatingRequestDTO);
        verify(this.mockMovieAPIRepository).findByTitleIgnoreCase(eq(fakeMovieTitle));
        verify(this.mockMovieAPISourceService, never()).getMovieAPISourceDTO(eq(this.fakeApiKey), eq(fakeMovieTitle));
        verify(this.mockMovieMappers, never()).movieAPISourceDTOToMovieAPIEntity(eq(fakeMovieAPISourceDTO));
        verify(this.mockRatingMappers).ratingRequestDTORatingEntity(eq(fakeRatingRequestDTO), eq(fakeAuthentication), eq(fakeMovieAPIEntity));
        verify(this.mockMovieAPIRepository).save(eq(fakeMovieAPIEntity));
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with mocked dependencies, " +
                  "WHEN getBestPictureMovieAPIDTO is called with a fake Movie Title set, " +
                   "AND MovieRepository returns null by calling findByTitleIgnoreCase method " +
                  "THEN it should throw a MovieAPINotFoundException with the fake Movie Title as message")
    public void givenMovieAPISourceServiceWhenGetBestPictureMovieAPIDTOCalledMovieRepositoryReturnsNullThenThrowMovieAPINotFoundException() {
        final var fakeMovieTitle = "Fake Movie Title";
        assertThrows(MovieAPINotFoundException.class, () -> movieAPISourceService.getBestPictureMovieAPIDTO(fakeMovieTitle), fakeMovieTitle);
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with mocked dependencies, " +
                  "WHEN getBestPictureMovieAPIDTO is called with a fake Movie Title set, " +
                   "AND MovieRepository returns a fake MovieAPIEntity by calling findByTitleIgnoreCase method " +
                  "THEN it should return the expected actual BestPictureMovieDTO instance")
    public void givenMovieAPISourceServiceWhenGetBestPictureMovieAPIDTOCalledThenShouldReturnExpectedActualMovieAPIDTOInstance() {
        final var fakeMovieTitle = "Fake Movie Title";
        final var fakeMovieAPIEntity = new MovieAPIEntity();
        final var fakeMovieAPIDTO = new BestPictureMovieDTO();

        when(mockMovieAPIRepository.findByTitleIgnoreCase(eq(fakeMovieTitle))).thenReturn(fakeMovieAPIEntity);
        when(mockMovieMappers.movieAPIEntityToBestPictureMovieDTO(eq(fakeMovieAPIEntity))).thenReturn(fakeMovieAPIDTO);

        final var actualMovieAPIDTo = movieAPISourceService.getBestPictureMovieAPIDTO(fakeMovieTitle);
        assertNotNull(actualMovieAPIDTo);
        verify(mockMovieAPIRepository).findByTitleIgnoreCase(eq(fakeMovieTitle));
        verify(mockMovieMappers).movieAPIEntityToBestPictureMovieDTO(eq(fakeMovieAPIEntity));
    }
}