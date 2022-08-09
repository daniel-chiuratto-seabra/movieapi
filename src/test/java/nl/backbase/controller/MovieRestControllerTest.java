package nl.backbase.controller;

import nl.backbase.IntegrationTest;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.model.MovieEntity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This {@link IntegrationTest} implementation, runs an integration test focusing in the "/v1/movie" based endpoints, testing
 * its operations
 *
 * @author Daniel Chiuratto Seabra
 * @since 07/08/2022
 */
class MovieRestControllerTest extends IntegrationTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("GIVEN a fake Movie collection that starts available in the database " +
                  "WHEN the Best Picture endpoint is called by each of those fake movies " +
                  "THEN the Movie information about it, including if it is an Oscar Winner or not should be returned as expected")
    public void givenFakeMovieCollectionWhenBestPictureEndpointCalledThenMovieInformationShouldBeReturnedAsExpected() throws Exception {
        // Here the buildFakeMovieDatabase method returns a List of MovieEntity without ratings in them, where all of them
        // are stored in the database as they won the Best Picture Oscar, so all the iterated movies below are movies
        // that won as Best Picture in Oscar
        final var fakeMovieEntityList = buildFakeMovieDatabase(false);
        for (final var fakeMovieEntity : fakeMovieEntityList) {
            // Then it is requested to the server to retrieve if the corresponding Movie Title of the corresponding
            // MovieEntity won a Best Picture Oscar
            final var mvcResult = this.mockMvc.perform(get(V1_MOVIE_BEST_PICTURE_ENDPOINT).accept(MediaType.APPLICATION_JSON_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, this.token)
                            .param(MOVIE_TITLE, fakeMovieEntity.getTitle()))
                    // Here is expected a Status OK meaning that yes, the Movie won a Best Picture Oscar
                    .andExpect(status().isOk()).andReturn();
            // Below the returned payload is asserted to confirm if the returned movie title and oscar winner fields match as expected
            final var movieDTO = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), BestPictureMovieDTO.class);
            assertEquals(fakeMovieEntity.getTitle(), movieDTO.getTitle());
            assertEquals(fakeMovieEntity.getBestPictureOscarWinner() ? MovieMappers.BEST_PICTURE_OSCAR_WINNER_YES : MovieMappers.BEST_PICTURE_OSCAR_WINNER_NO, movieDTO.getBestPictureOscarWinner());
        }
    }

    @Test
    @DisplayName("GIVEN a fake Movie collection with Random rating that starts available in the database " +
                  "WHEN the Top 10 list is requested " +
                  "THEN a list of most rated Movies in decreasing order by Rating average and Box Office should be returned as expected")
    public void givenFakeMovieCollectionWithRandomRatingWhenTop10ListRequestedThenListMostRatedMoviesDecreasingOrderByRatingAverageAndBoxOfficeShouldReturn() throws Exception {
        // Here a set of Movie Entity list is generated, but with ratings, and they are stored in the database as they have won
        // the Best Picture
        final var expectedFakeMovieEntityList = buildFakeMovieDatabase(true);

        // And then the Top 10 list is requested form the endpoint
        final var mvcResult = this.mockMvc.perform(get(V1_MOVIE_TOP_10_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.token)).andExpect(status().isOk()).andReturn();

        // And in the end the payload is deserialized into a List of MovieTop10DTO classes to assert if each of the returned
        // Movie is as expected
        final List<MovieTop10DTO> actualMovieSummaryDTOList = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), objectMapper.getTypeFactory().constructCollectionType(List.class, MovieTop10DTO.class));
        // Here the amount of returned elements is compared with the expected amount
        assertEquals(expectedFakeMovieEntityList.size(), actualMovieSummaryDTOList.size());
        // Here each movie is asserted to see if it is as expected in the expected order
        IntStream.range(0, expectedFakeMovieEntityList.size()).forEach(index -> {
            final var expectedMovieEntity = expectedFakeMovieEntityList.get(index);
            final var actualMovieSummaryDTO = actualMovieSummaryDTOList.get(index);
            assertEquals(expectedMovieEntity.getTitle(), actualMovieSummaryDTO.getTitle());
            assertEquals(expectedMovieEntity.getBoxOffice().doubleValue(), actualMovieSummaryDTO.getBoxOffice().doubleValue());
            assertEquals(expectedMovieEntity.getBestPictureOscarWinner(), "YES".equals(actualMovieSummaryDTO.getBestPictureOscarWinner()));
        });
    }

    /**
     * This method generates a {@link List} of fake {@link MovieEntity}, where the
     * {@param isWithRatings} defines if they need to have a {@link Collection} of {@link RatingEntity}
     * or not, for testing purposes
     *
     * @param isWithRatings {@link Boolean} that sets if the {@link MovieEntity} {@link List} should contain
     *                                     {@link RatingEntity} or not
     * @return {@link List} of {@link MovieEntity}
     *
     * @author Daniel Chiuratto Seabra
     * @since 07/08/2022
     */
    private List<MovieEntity> buildFakeMovieDatabase(final boolean isWithRatings) {

        // First all the remaining Movies must be removed so there will be no conflicts with other
        // test data
        this.movieRepository.deleteAll();

        // An ArrayList is instantiated
        final var fakeMovieList = new ArrayList<MovieEntity>();

        // Then an iteration happens 5 times to create 5 fake MovieEntity to build the List to be returned for testing
        IntStream.range(0, 5).forEach(index -> {
            // Following fake values are set
            final var fakeMovieEntity = new MovieEntity();
            fakeMovieEntity.setTitle(String.format("Fake Movie %s", index));
            fakeMovieEntity.setBoxOffice(new BigDecimal(1000 * index));
            fakeMovieEntity.setBestPictureOscarWinner(index % 2 == 0);

            // And in this if block, if it is to generate fake ratings for the corresponding Fake Movie,
            // 5 of them will be created
            if (isWithRatings) {
                final var fakeRatingCollection = new ArrayList<RatingEntity>();
                IntStream.range(0, 5).forEach(index2 -> {
                    final var fakeRatingEntity = new RatingEntity();
                    fakeRatingEntity.setMovieEntity(fakeMovieEntity);
                    fakeRatingEntity.setSource(String.format("Fake Source %d", index2));
                    fakeRatingEntity.setValue(index2 % 2 == 0 ? (double) index2 : ((double) 10 - index2) - index);
                    fakeRatingCollection.add(fakeRatingEntity);
                });
                fakeMovieEntity.setRatings(fakeRatingCollection);
            }
            // And then the fake Movie will be added into the List
            fakeMovieList.add(fakeMovieEntity);
        });

        // The list is saved into the database
        this.movieRepository.saveAll(fakeMovieList);

        // And the is returned to be used as a source for parsing in the tests
        return fakeMovieList;
    }
}