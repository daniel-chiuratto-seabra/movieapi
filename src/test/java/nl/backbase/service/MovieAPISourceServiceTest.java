package nl.backbase.service;

import nl.backbase.controller.exception.MovieAPINotFoundException;
import nl.backbase.controller.exception.MovieAPISourceServiceException;
import nl.backbase.dto.source.MovieAPISourceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieAPISourceServiceTest {

    private MovieAPISourceService movieAPISourceService;

    private RestTemplate mockRestTemplate;

    @BeforeEach
    private void setUp() {
        this.mockRestTemplate = mock(RestTemplate.class);

        final var mockRestTemplateBuilder = mock(RestTemplateBuilder.class);
        when(mockRestTemplateBuilder.build()).thenReturn(this.mockRestTemplate);

        final var fakeMovieSourceAPIURL = "Fake Movie Source API URL";
        this.movieAPISourceService = new MovieAPISourceService(fakeMovieSourceAPIURL, mockRestTemplateBuilder);
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with fake and mocked dependencies " +
                  "WHEN the getMovieAPISourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method throws a HttpClientErrorException " +
                  "THEN a MovieAPISourceServiceException should be thrown")
    public void givenMovieAPISourceServiceWithFakeWhenGetMovieAPISourceDTOFromCSVFileIsCalledAndExchangeThrowsHttpClientErrorExceptionTthenMovieAPISourceServiceExceptionShouldBeThrown() {
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(MovieAPISourceServiceException.class, () -> this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info"));
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with fake and mocked dependencies " +
                  "WHEN the getMovieAPISourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status different than 200 " +
                  "THEN a MovieAPISourceServiceException should be thrown")
    public void givenMovieAPISourceServiceWhenGetMovieAPISourceDTOFromCSVFileIsCalledThenMovieAPISourceServiceExceptionShouldBeThrown() {
        final var fakeResponseEntity = new ResponseEntity<MovieAPISourceDTO>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(fakeResponseEntity);
        assertThrows(MovieAPISourceServiceException.class, () -> this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info"));
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with fake and mocked dependencies " +
                  "WHEN the getMovieAPISourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a null response body " +
                  "THEN a MovieAPISourceServiceException should be thrown")
    public void givenMovieAPISourceServiceWithFakeMockedDependenciesWhenGetMovieAPISourceDTOFromCSVFileIsCalledWithTestTemplateReturnsNullBodyThenMovieAPISourceServiceExceptionShouldBeThrown() {
        final var fakeResponseEntity = new ResponseEntity<MovieAPISourceDTO>(null, null, HttpStatus.OK);
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(fakeResponseEntity);
        assertThrows(MovieAPISourceServiceException.class, () -> this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info"));
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with fake and mocked dependencies " +
                  "WHEN the getMovieAPISourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a response body containing a response field as true " +
                  "THEN an actual MovieAPISourceDTO should be returned with the expected values")
    public void givenMovieAPISourceServiceWhenGetMovieAPISourceDTOFromCSVFileIsCalledThenMovieAPISourceDTOShouldBeReturnedWithTheExpectedValues() {
        final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
        expectedFakeMovieAPISourceDTO.setResponse("true");
        final var fakeResponseEntity = new ResponseEntity<>(expectedFakeMovieAPISourceDTO, null, HttpStatus.OK);
        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(fakeResponseEntity);
        final var actualMovieAPISourceDTOFromCSVFile = this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info");
        assertNotNull(actualMovieAPISourceDTOFromCSVFile);
        assertEquals(expectedFakeMovieAPISourceDTO.getResponse(), actualMovieAPISourceDTOFromCSVFile.getResponse());
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with fake and mocked dependencies " +
                  "WHEN the getMovieAPISourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a response body containing a response field as false " +
                  "THEN another RestTemplate exchange call should be made " +
                   "AND with a response body with response field as true " +
                   "AND an actual MovieAPISourceDTO with the expected values")
    public void givenMovieAPISourceServiceWhenGetMovieAPISourceDTOFromCSVFileReturns200ResponseFalseAnotherExchangeResponseTrueActualMovieAPISourceDTOExpectedValues() {
        final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
        expectedFakeMovieAPISourceDTO.setResponse("true");

        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenAnswer(new Answer<ResponseEntity<?>>() {
            boolean firstTime = true;
            @Override
            public ResponseEntity<?> answer(final InvocationOnMock invocation) {
                if (firstTime) {
                    firstTime = false;
                    final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
                    expectedFakeMovieAPISourceDTO.setResponse("false");
                    return new ResponseEntity<>(expectedFakeMovieAPISourceDTO, null, HttpStatus.OK);
                }
                return new ResponseEntity<>(expectedFakeMovieAPISourceDTO, null, HttpStatus.OK);
            }
        });

        final var actualMovieAPISourceDTOFromCSVFile = this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile("Fake API", "Fake Movie Title", "Fake Additional Info");
        assertNotNull(actualMovieAPISourceDTOFromCSVFile);
        assertEquals(expectedFakeMovieAPISourceDTO.getResponse(), actualMovieAPISourceDTOFromCSVFile.getResponse());
    }

    @Test
    @DisplayName("GIVEN a MovieAPISourceService with fake and mocked dependencies " +
                  "WHEN the getMovieAPISourceDTOFromCSVFile is called with fake values " +
                   "AND the RestTemplate exchange method returns a http status with status 200 " +
                   "AND a response body containing a response field as false " +
                  "THEN another RestTemplate exchange call should be made " +
                   "AND with a response body with response field as false " +
                   "AND a MovieAPINotFoundException should be thrown")
    public void givenAMovieAPISourceServiceWhenGetMovieAPISourceDTOFromCSVFileCalledThenAnotherExchangeWithResponseFieldMovieAPINotFoundExceptionShouldBeThrown() {
        final var expectedFakeMovieTitle = "Fake Movie Title";
        final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
        expectedFakeMovieAPISourceDTO.setResponse("false");

        when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenAnswer(new Answer<ResponseEntity<?>>() {
            boolean firstTime = true;
            @Override
            public ResponseEntity<?> answer(final InvocationOnMock invocation) {
                if (firstTime) {
                    firstTime = false;
                    final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
                    expectedFakeMovieAPISourceDTO.setResponse("false");
                    return new ResponseEntity<>(expectedFakeMovieAPISourceDTO, null, HttpStatus.OK);
                }
                return new ResponseEntity<>(expectedFakeMovieAPISourceDTO, null, HttpStatus.OK);
            }
        });

        assertThrows(MovieAPINotFoundException.class, () -> this.movieAPISourceService.getMovieAPISourceDTOFromCSVFile("Fake API", expectedFakeMovieTitle, "Fake Additional Info"), expectedFakeMovieTitle);
    }
}
