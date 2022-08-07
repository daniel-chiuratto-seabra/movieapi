package nl.backbase.controller;

import nl.backbase.IntegrationTest;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RatingAPIRestControllerTest extends IntegrationTest {

    @Test
    @DisplayName("GIVEN a Rating to be posted by the Fake User that does not exist in the database " +
                  "WHEN the user post the Rating " +
                  "THEN the API should look for the Movie data in the external API source " +
                  "AND the API should return the Rating request after being successfully posted")
    public void givenRatingToBePostedWhenUserPostThenAPIReturnRatingRequestAfterSuccessfullyPosted() throws Exception {
        final var expectedRatingRequestDTO = new RatingRequestDTO();
        expectedRatingRequestDTO.setMovieTitle("Fake Movie");
        expectedRatingRequestDTO.setValue("10");

        final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
        expectedFakeMovieAPISourceDTO.setTitle("Fake Movie");
        expectedFakeMovieAPISourceDTO.setBoxOffice("$234,543,231");
        expectedFakeMovieAPISourceDTO.setResponse("true");

        final var expectedFakeResponseEntity = ResponseEntity.ok(expectedFakeMovieAPISourceDTO);

        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(expectedFakeResponseEntity);

        final var mockMvc = this.mockMvc.perform(post(V1_RATING_ENDPOINT).header(HttpHeaders.AUTHORIZATION, this.token)
                                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                                    .accept(MediaType.APPLICATION_JSON)
                                                                                    .content(asJsonString(expectedRatingRequestDTO)))
                                                                .andExpect(status().isOk()).andReturn();
        final var contentAsString = mockMvc.getResponse().getContentAsString();
        assertNotNull(contentAsString);

        final var actualRatingRequestDTO = this.objectMapper.readValue(contentAsString, RatingRequestDTO.class);
        assertEquals(expectedRatingRequestDTO.getMovieTitle(), actualRatingRequestDTO.getMovieTitle());
        assertEquals(expectedRatingRequestDTO.getValue(), actualRatingRequestDTO.getValue());
    }

}