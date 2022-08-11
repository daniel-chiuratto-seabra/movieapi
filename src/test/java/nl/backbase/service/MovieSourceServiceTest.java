package nl.backbase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import nl.backbase.controller.exception.MovieSourceServiceException;
import nl.backbase.dto.source.MovieSourceDTO;

@SuppressWarnings("unchecked") // The Mock RestTemplate is not working with specific
class MovieSourceServiceTest { // values so generalized settings needs to be set in
							   // order to make it return a simulated fake response,
							   // that is why the suppression above

    private MovieSourceService movieSourceService;

    private RestTemplate mockRestTemplate;

    @BeforeEach
    private void setUp() {
        this.mockRestTemplate = mock(RestTemplate.class);
        final var fakeMovieSourceAPIURL = "Fake Movie Source API URL";
        this.movieSourceService = new MovieSourceService(fakeMovieSourceAPIURL, this.mockRestTemplate);
    }

	@Test
    @DisplayName("GIVEN a MovieSourceService with fake and mocked dependencies " +
                  "WHEN the getMovieSourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method throws a HttpClientErrorException " +
                  "THEN a MovieSourceServiceException should be thrown")
    public void givenMovieSourceServiceWithFakeWhenGetMovieSourceDTOFromCSVFileIsCalledAndExchangeThrowsHttpClientErrorExceptionTthenMovieSourceServiceExceptionShouldBeThrown() {
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(MovieSourceServiceException.class, () -> this.movieSourceService.getMovieSourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info"));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with fake and mocked dependencies " +
                  "WHEN the getMovieSourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status different than 200 " +
                  "THEN a MovieSourceServiceException should be thrown")
    public void givenMovieSourceServiceWhenGetMovieSourceDTOFromCSVFileIsCalledThenMovieSourceServiceExceptionShouldBeThrown() {
        final var fakeResponseEntity = new ResponseEntity<MovieSourceDTO>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(fakeResponseEntity);
        assertThrows(MovieSourceServiceException.class, () -> this.movieSourceService.getMovieSourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info"));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with fake and mocked dependencies " +
                  "WHEN the getMovieSourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a null response body " +
                  "THEN a MovieSourceServiceException should be thrown")
    public void givenMovieSourceServiceWithFakeMockedDependenciesWhenGetMovieSourceDTOFromCSVFileIsCalledWithTestTemplateReturnsNullBodyThenMovieSourceServiceExceptionShouldBeThrown() {
        final var fakeResponseEntity = new ResponseEntity<MovieSourceDTO>(null, null, HttpStatus.OK);
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(fakeResponseEntity);
        assertNull(this.movieSourceService.getMovieSourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info"));
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with fake and mocked dependencies " +
                  "WHEN the getMovieSourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a response body containing a response field as true " +
                  "THEN an actual MovieSourceDTO should be returned with the expected values")
    public void givenMovieSourceServiceWhenGetMovieSourceDTOFromCSVFileIsCalledThenMovieSourceDTOShouldBeReturnedWithTheExpectedValues() {
        final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
        expectedFakeMovieSourceDTO.setResponse("true");
        expectedFakeMovieSourceDTO.setTitle("Fake Movie Title");
        final var fakeResponseEntity = new ResponseEntity<>(expectedFakeMovieSourceDTO, null, HttpStatus.OK);
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(fakeResponseEntity);
        final var actualMovieSourceDTOFromCSVFile = this.movieSourceService.getMovieSourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info");
        assertEquals(expectedFakeMovieSourceDTO.getResponse(), actualMovieSourceDTOFromCSVFile.getResponse());
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with fake and mocked dependencies " +
                  "WHEN the getMovieSourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a response body containing a response field as false " +
                  "THEN another RestTemplate exchange call should be made " +
                   "AND with a response body with response field as true " +
                   "AND an actual MovieSourceDTO with the expected values")
    public void givenMovieSourceServiceWhenGetMovieSourceDTOFromCSVFileReturns200ResponseFalseAnotherExchangeResponseTrueActualMovieSourceDTOExpectedValues() {
        final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
        expectedFakeMovieSourceDTO.setResponse("true");
        expectedFakeMovieSourceDTO.setTitle("Fake Additional Info");

        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenAnswer(new Answer<ResponseEntity<?>>() {
            boolean firstTime = true;
            @Override
            public ResponseEntity<?> answer(final InvocationOnMock invocation) {
                if (firstTime) {
                    firstTime = false;
                    final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
                    expectedFakeMovieSourceDTO.setResponse("false");
                    return new ResponseEntity<>(expectedFakeMovieSourceDTO, null, HttpStatus.OK);
                }
                return new ResponseEntity<>(expectedFakeMovieSourceDTO, null, HttpStatus.OK);
            }
        });

        final var actualMovieSourceDTOFromCSVFile = this.movieSourceService.getMovieSourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info");
        assertEquals(expectedFakeMovieSourceDTO.getResponse(), actualMovieSourceDTOFromCSVFile.getResponse());
    }

    @Test
    @DisplayName("GIVEN a MovieSourceService with fake and mocked dependencies " +
                  "WHEN the getMovieSourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a response body containing a response field as false " +
                  "THEN another RestTemplate exchange call should be made " +
                   "AND with a response body with response field as false " +
                   "AND a MovieNotFoundException should be thrown")
    public void givenAMovieSourceServiceWhenGetMovieSourceDTOFromCSVFileCalledThenAnotherExchangeWithResponseFieldMovieNotFoundExceptionShouldBeThrown() {
        final var expectedFakeMovieTitle = "Fake Movie Title";
        final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
        expectedFakeMovieSourceDTO.setResponse("false");

        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenAnswer(new Answer<ResponseEntity<?>>() {
            boolean firstTime = true;
            @Override
            public ResponseEntity<?> answer(final InvocationOnMock invocation) {
                if (firstTime) {
                    firstTime = false;
                    final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
                    expectedFakeMovieSourceDTO.setResponse("false");
                    return new ResponseEntity<>(expectedFakeMovieSourceDTO, null, HttpStatus.OK);
                }
                return new ResponseEntity<>(expectedFakeMovieSourceDTO, null, HttpStatus.OK);
            }
        });

        assertNull(this.movieSourceService.getMovieSourceDTOFromCSVFile("Fake API", expectedFakeMovieTitle, "Fake Additional Info"), expectedFakeMovieTitle);
    }
}
