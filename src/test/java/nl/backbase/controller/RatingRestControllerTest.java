package nl.backbase.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import nl.backbase.IntegrationTest;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieSourceDTO;

/**
 * This {@link IntegrationTest} implementation, runs an integration test focusing in the "/v1/rating" endpoint, testing
 * its operations
 *
 * @author Daniel Chiuratto Seabra
 * @since 07/08/2022
 */
@SuppressWarnings("unchecked") 							 // The Mock RestTemplate is not working with specific
class RatingRestControllerTest extends IntegrationTest { // values so generalized settings needs to be set in
														 // order to make it return a simulated fake response,
														 // that is why the suppression above

    @Test
    @DisplayName("GIVEN a Rating to be posted by the Fake User that does not exist in the database " +
                  "WHEN the user post the Rating " +
                  "THEN the API should look for the Movie data in the external API source " +
                  "AND the API should return the Rating request after being successfully posted")
    public void givenRatingToBePostedWhenUserPostThenAPIReturnRatingRequestAfterSuccessfullyPosted() throws Exception {
        // Here a fake Rating Request is instantiated with fake random values
        final var expectedRatingRequestDTO = new RatingRequestDTO();
        expectedRatingRequestDTO.setMovieTitle("Fake Movie");
        expectedRatingRequestDTO.setValue("10");

        // Then a fake Movie Source is instantiated as well, representing a response from the mocked Rest Template
        // that requests data to the external Movie Source API
        final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
        expectedFakeMovieSourceDTO.setTitle("Fake Movie");
        expectedFakeMovieSourceDTO.setBoxOffice("$234,543,231");
        expectedFakeMovieSourceDTO.setResponse("true");

        // Here a ResponseEntity is instantiated with the fake Movie Source instantiated above, to simulate the RestTemplate
        // returning it
        final var expectedFakeResponseEntity = ResponseEntity.ok(expectedFakeMovieSourceDTO);

        // Here there is a WHEN, with generics everywhere because for some reason it is not working if specific data is set as expected
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(expectedFakeResponseEntity);

        // Then a call is made requesting to Rate the movie set in the Rating Request instantiated above in the "expectedRatingRequestDTO" variable
        final var mockMvc = this.mockMvc.perform(post(V1_RATING_ENDPOINT).header(HttpHeaders.AUTHORIZATION, this.token)
                                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                                    .accept(MediaType.APPLICATION_JSON)
                                                                                    .content(asJsonString(expectedRatingRequestDTO)))
                                                                .andExpect(status().isOk()).andReturn();

        // Later it is deserialized into RatingRequestDTO as returned by the API, that means that the Rating has been successfully stored
        final var actualRatingRequestDTO = this.objectMapper.readValue(mockMvc.getResponse().getContentAsByteArray(), RatingRequestDTO.class);
        assertEquals(expectedRatingRequestDTO.getMovieTitle(), actualRatingRequestDTO.getMovieTitle());
        assertEquals(expectedRatingRequestDTO.getValue(), actualRatingRequestDTO.getValue());
    }

}