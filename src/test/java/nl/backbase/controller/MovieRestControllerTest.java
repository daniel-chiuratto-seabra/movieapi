package nl.backbase.controller;

import nl.backbase.IntegrationTest;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.model.MovieEntity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MovieRestControllerTest extends IntegrationTest {

    @Autowired
    private MovieRepository movieRepository;


    @Test
    @DisplayName("GIVEN a fake Movie collection that starts available in the database " +
                  "WHEN the Best Picture endpoint is called by each of those fake movies " +
                  "THEN the Movie information about it, including if it is an Oscar Winner or not should be returned as expected")
    public void givenFakeMovieCollectionWhenBestPictureEndpointCalledThenMovieInformationShouldBeReturnedAsExpected() {
        buildFakeMovieDatabase(false).forEach(movieEntity -> {
            try {
                final var mvcResult = this.mockMvc.perform(get(V1_MOVIE_BEST_PICTURE_ENDPOINT).accept(MediaType.APPLICATION_JSON_VALUE)
                                                                                                         .header(HttpHeaders.AUTHORIZATION, this.token)
                                                                                                         .param(MOVIE_TITLE, movieEntity.getTitle()))
                        .andExpect(status().isOk()).andReturn();

                assertNotNull(mvcResult.getResponse().getContentAsString());
                assertFalse(mvcResult.getResponse().getContentAsString().trim().isEmpty());

                final var movieDTO = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BestPictureMovieDTO.class);
                assertEquals(movieEntity.getTitle(), movieDTO.getTitle());
                assertEquals(movieEntity.getOscarWinner() ? "YES" : "NO", movieDTO.getOscarWinner());
            } catch (final Exception e) {
                Assertions.fail(e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("GIVEN a fake Movie collection with Random rating that starts available in the database " +
                  "WHEN the Top 10 list is requested " +
                  "THEN a list of most rated Movies in decreasing order by Rating average and Box Office should be returned as expected")
    public void givenFakeMovieCollectionWithRandomRatingWhenTop10ListRequestedThenTistMostRatedMoviesDecreasingOrderByRatingAverageAndBoxOfficeShouldReturn() throws Exception {
        final var expectedFakeMovieEntityList = buildFakeMovieDatabase(true);

        final var mvcResult = this.mockMvc.perform(get(V1_MOVIE_TOP_10_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.token)).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse().getContentAsString());
        assertFalse(mvcResult.getResponse().getContentAsString().trim().isEmpty());

        final List<MovieTop10DTO> actualMovieSummaryDTOList = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, MovieTop10DTO.class));
        assertEquals(expectedFakeMovieEntityList.size(), actualMovieSummaryDTOList.size());
        IntStream.range(0, expectedFakeMovieEntityList.size()).forEach(index -> {
            final var expectedMovieEntity = expectedFakeMovieEntityList.get(index);
            final var actualMovieSummaryDTO = actualMovieSummaryDTOList.get(index);
            assertEquals(expectedMovieEntity.getTitle(), actualMovieSummaryDTO.getTitle());
            assertEquals(expectedMovieEntity.getBoxOffice().doubleValue(), actualMovieSummaryDTO.getBoxOffice().doubleValue());
            assertEquals(expectedMovieEntity.getOscarWinner(), "YES".equals(actualMovieSummaryDTO.getOscarWinner()));
        });
    }

    private List<MovieEntity> buildFakeMovieDatabase(final boolean isWithRatings) {
        this.movieRepository.deleteAll();

        final var fakeMovieCollection = new ArrayList<MovieEntity>();

        IntStream.range(0, 5).forEach(index -> {
            final var fakeMovieEntity = new MovieEntity();
            fakeMovieEntity.setTitle(String.format("Fake Movie %s", index));
            fakeMovieEntity.setBoxOffice(new BigDecimal(1000 * index));
            fakeMovieEntity.setOscarWinner(index % 2 == 0);

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

            fakeMovieCollection.add(fakeMovieEntity);
        });

        this.movieRepository.saveAll(fakeMovieCollection);
        return fakeMovieCollection;
    }
}