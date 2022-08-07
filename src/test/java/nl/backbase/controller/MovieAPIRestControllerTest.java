package nl.backbase.controller;

import nl.backbase.IntegrationTest;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import nl.backbase.repository.MovieAPIRepository;
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

class MovieAPIRestControllerTest extends IntegrationTest {

    @Autowired
    private MovieAPIRepository movieAPIRepository;


    @Test
    @DisplayName("GIVEN a fake Movie collection that starts available in the database " +
                  "WHEN the Best Picture endpoint is called by each of those fake movies " +
                  "THEN the Movie information about it, including if it is an Oscar Winner or not should be returned as expected")
    public void givenFakeMovieCollectionWhenBestPictureEndpointCalledThenMovieInformationShouldBeReturnedAsExpected() {
        buildFakeMovieDatabase(false).forEach(movieAPIEntity -> {
            try {
                final var mvcResult = this.mockMvc.perform(get(V1_MOVIE_BEST_PICTURE_ENDPOINT).accept(MediaType.APPLICATION_JSON_VALUE)
                                                                                                         .header(HttpHeaders.AUTHORIZATION, this.token)
                                                                                                         .param(MOVIE_TITLE, movieAPIEntity.getTitle()))
                        .andExpect(status().isOk()).andReturn();

                assertNotNull(mvcResult.getResponse());
                assertNotNull(mvcResult.getResponse().getContentAsString());
                assertFalse(mvcResult.getResponse().getContentAsString().trim().isEmpty());

                final var movieAPIDTO = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BestPictureMovieDTO.class);
                assertEquals(movieAPIEntity.getTitle(), movieAPIDTO.getTitle());
                assertEquals(movieAPIEntity.getOscarWinner() ? "YES" : "NO", movieAPIDTO.getOscarWinner());
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
        final var expectedFakeMovieAPIEntityList = buildFakeMovieDatabase(true);

        final var mvcResult = this.mockMvc.perform(get(V1_MOVIE_TOP_10_ENDPOINT).accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.token)).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        assertNotNull(mvcResult.getResponse().getContentAsString());
        assertFalse(mvcResult.getResponse().getContentAsString().trim().isEmpty());

        final List<MovieTop10DTO> actualMovieAPISummaryDTOList = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, MovieTop10DTO.class));
        assertEquals(expectedFakeMovieAPIEntityList.size(), actualMovieAPISummaryDTOList.size());
        IntStream.range(0, expectedFakeMovieAPIEntityList.size()).forEach(index -> {
            final var expectedMovieAPIEntity = expectedFakeMovieAPIEntityList.get(index);
            final var actualMovieAPISummaryDTO = actualMovieAPISummaryDTOList.get(index);
            assertEquals(expectedMovieAPIEntity.getTitle(), actualMovieAPISummaryDTO.getTitle());
            assertEquals(expectedMovieAPIEntity.getBoxOffice().doubleValue(), actualMovieAPISummaryDTO.getBoxOffice().doubleValue());
            assertEquals(expectedMovieAPIEntity.getOscarWinner(), "YES".equals(actualMovieAPISummaryDTO.getOscarWinner()));
        });
    }

    private List<MovieAPIEntity> buildFakeMovieDatabase(final boolean isWithRatings) {
        this.movieAPIRepository.deleteAll();

        final var fakeMovieAPICollection = new ArrayList<MovieAPIEntity>();

        IntStream.range(0, 5).forEach(index -> {
            final var fakeMovieAPIEntity = new MovieAPIEntity();
            fakeMovieAPIEntity.setTitle(String.format("Fake Movie %s", index));
            fakeMovieAPIEntity.setBoxOffice(new BigDecimal(1000 * index));
            fakeMovieAPIEntity.setOscarWinner(index % 2 == 0);

            if (isWithRatings) {
                final var fakeRatingCollection = new ArrayList<RatingEntity>();
                IntStream.range(0, 5).forEach(index2 -> {
                    final var fakeRatingEntity = new RatingEntity();
                    fakeRatingEntity.setMovieAPIEntity(fakeMovieAPIEntity);
                    fakeRatingEntity.setSource(String.format("Fake Source %d", index2));
                    fakeRatingEntity.setValue(index2 % 2 == 0 ? (double) index2 : ((double) 10 - index2) - index);
                    fakeRatingCollection.add(fakeRatingEntity);
                });
                fakeMovieAPIEntity.setRatings(fakeRatingCollection);
            }

            fakeMovieAPICollection.add(fakeMovieAPIEntity);
        });

        this.movieAPIRepository.saveAll(fakeMovieAPICollection);
        return fakeMovieAPICollection;
    }
}