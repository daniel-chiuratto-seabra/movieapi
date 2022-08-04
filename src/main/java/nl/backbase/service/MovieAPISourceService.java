package nl.backbase.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieAPINotFoundException;
import nl.backbase.controller.exception.MovieAPISourceServiceException;
import nl.backbase.dto.source.MovieAPISourceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Slf4j
@Service
public class MovieAPISourceService {

    private static final String API_KEY_PARAM_NAME = "apiKey";
    private static final String MOVIE_TITLE_PARAM_NAME = "t";
    private static final String DATA_TYPE_PARAM_NAME = "type";
    private static final String MOVIE_DATA_TYPE = "movie";

    private final String movieSourceApiUrl;
    private final RestTemplate restTemplate;

    public MovieAPISourceService(@Value("${movie.api.url}") final String movieSourceApiUrl, final RestTemplateBuilder restTemplateBuilder) {
        this.movieSourceApiUrl = movieSourceApiUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    public MovieAPISourceDTO getMovieAPISourceDTO(final String apiKey, final String movieTitle) {
        return getMovieAPISourceDTOFromCSVFile(apiKey, movieTitle, null);
    }

    public MovieAPISourceDTO getMovieAPISourceDTOFromCSVFile(final String apiKey, final String movieTitle, final String additionalInfo) {
        final var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        final var requestEntity = new HttpEntity<>(httpHeaders);

        var urlParametersMap = Map.ofEntries(entry(API_KEY_PARAM_NAME, apiKey), entry(MOVIE_TITLE_PARAM_NAME, movieTitle), entry(DATA_TYPE_PARAM_NAME, MOVIE_DATA_TYPE));
        final var urlParams = buildParamsPlaceholders(urlParametersMap);
        try {
            var responseEntity = this.restTemplate.exchange(this.movieSourceApiUrl + urlParams, HttpMethod.GET, requestEntity, MovieAPISourceDTO.class, urlParametersMap);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                var movieSourceDTO = responseEntity.getBody();
                if (movieSourceDTO != null && movieSourceDTO.getResponse() != null && Boolean.TRUE == Boolean.valueOf(movieSourceDTO.getResponse())) {
                    return movieSourceDTO;
                } else if (movieSourceDTO != null && Boolean.FALSE == Boolean.valueOf(movieSourceDTO.getResponse())) {
                    urlParametersMap = new HashMap<>(urlParametersMap);
                    urlParametersMap.put(MOVIE_TITLE_PARAM_NAME, additionalInfo);
                    log.info("The movie {} has not been found, trying to load the movie {} available in the 'additionalInfo' field", movieTitle, additionalInfo);
                    responseEntity = this.restTemplate.exchange(this.movieSourceApiUrl + urlParams, HttpMethod.GET, requestEntity, MovieAPISourceDTO.class, urlParametersMap);
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        movieSourceDTO = responseEntity.getBody();
                        if (movieSourceDTO != null && movieSourceDTO.getResponse() != null && Boolean.TRUE == Boolean.valueOf(movieSourceDTO.getResponse())) {
                            log.info("Movie {} from the 'additionalInfo' field successfully retrieved from the API", additionalInfo);
                            return movieSourceDTO;
                        } else {
                            throw new MovieAPINotFoundException(movieTitle);
                        }
                    }
                }
            }
            throw new MovieAPISourceServiceException(String.format("The external Movie API did not return any body. It returned Http Status %d: %s", responseEntity.getStatusCode().value(), responseEntity.getStatusCode().name()));
        } catch (final HttpClientErrorException httpClientErrorException) {
            throw new MovieAPISourceServiceException(String.format("The external API returned an error %d (%s)", httpClientErrorException.getStatusCode().value(), httpClientErrorException.getStatusCode().name()), httpClientErrorException);
        }
    }

    private String buildParamsPlaceholders(final Map<String, String> urlParametersMap) {
        return "?" + urlParametersMap.keySet().stream().map(s -> String.format("%s={%s}", s, s)).collect(Collectors.joining("&"));
    }
}

