package nl.backbase.service;

import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.RatingMappers;
import nl.backbase.repository.MovieAPIRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MovieAPIServiceTest {

    private static MovieAPIRepository mockMovieAPIRepository;
    private static MovieAPISourceService mockMovieAPISourceService;
    private static MovieMappers mockMovieMappers;
    private static RatingMappers mockRatingMappers;
    private static String fakeApiKey;

    private static MovieAPIService movieApiSourceService;

    @BeforeAll
    public static void setUpAll() {
        mockMovieAPIRepository = Mockito.mock(MovieAPIRepository.class);
        mockMovieAPISourceService = Mockito.mock(MovieAPISourceService.class);
        mockMovieMappers = Mockito.mock(MovieMappers.class);
        mockRatingMappers = Mockito.mock(RatingMappers.class);
        fakeApiKey = "Fake Api Key";
        movieApiSourceService = new MovieAPIService(mockMovieAPIRepository, mockMovieAPISourceService, mockMovieMappers, mockRatingMappers, fakeApiKey);
    }

    @Test
    public void test() {
        final var actualMovieAPIDTO = movieApiSourceService.getBestPictureMovieAPIDTO("Fake Movie Title");
    }

}