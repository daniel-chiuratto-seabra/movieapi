package nl.backbase.service;

import nl.backbase.model.MovieSourceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class MovieSourceService {

    private static final String API_KEY_PARAM_NAME = "apiKey";
    private static final String MOVIE_TITLE_PARAM_NAME = "t";

    private final String movieSourceApiUrl;
    private final RestTemplate restTemplate;

    public MovieSourceService(@Value("${movie.api.url}") final String movieSourceApiUrl, final RestTemplate restTemplate) {
        this.movieSourceApiUrl = movieSourceApiUrl;
        this.restTemplate = restTemplate;
    }

    public MovieSourceDTO getMovieSourceDTO(final String apiKey, final String movieTitle) {
        final var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        final var requestEntity = new HttpEntity<>(httpHeaders);

        final var urlParametersMap = Map.ofEntries(entry(API_KEY_PARAM_NAME, apiKey), entry(MOVIE_TITLE_PARAM_NAME, movieTitle));
        final var urlParams = buildParamsPlaceholders(urlParametersMap);
        final var responseEntity = this.restTemplate.exchange(this.movieSourceApiUrl + urlParams, HttpMethod.GET, requestEntity, MovieSourceDTO.class, urlParametersMap);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        return null;
    }

    private String buildParamsPlaceholders(final Map<String, String> urlParametersMap) {
        return "?" + urlParametersMap.keySet().stream().map(s -> String.format("%s={%s}", s, s)).collect(Collectors.joining("&"));
    }
}

