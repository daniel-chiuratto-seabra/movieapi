package nl.backbase.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieSourceServiceException;
import nl.backbase.dto.source.MovieSourceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.entry;

/**
 * This {@link Service} is related in offering access to the external Movie Source API used to retrieve additional data
 * of each requested Movie
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Slf4j
@Service
public class MovieSourceService {

    private static final String API_KEY_PARAM_NAME = "apiKey";
    private static final String MOVIE_TITLE_PARAM_NAME = "t";
    private static final String DATA_TYPE_PARAM_NAME = "type";
    private static final String MOVIE_DATA_TYPE = "movie";

    private final String movieSourceApiUrl;
    private final RestTemplate restTemplate;

    public MovieSourceService(@Value("${movie.api.url}") final String movieSourceAPIUrl, final RestTemplate restTemplate) {
        this.movieSourceApiUrl = movieSourceAPIUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * This method request data of the given Movie Title with the API Key (set in the {@code movie.api.url} property)
     * that is needed to be able to actually request the Movie data from the API
     *
     * @param apiKey {@link String} representing the API Key needed to be able to retrieve data from the API
     * @param movieTitle {@link String} representing the Movie Title to be requested
     *
     * @return {@link MovieSourceDTO} instance with the returned data from the external Movie Source API
     * (returns {@code null} if the movie is not found)
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    public MovieSourceDTO getMovieSourceDTO(final String apiKey, final String movieTitle) {
        log.trace("Requesting Movie '{}' from the Movie source API", movieTitle);
        return getMovieSourceDTOFromCSVFile(apiKey, movieTitle, null);
    }

    /**
     * This method request data of the given Movie Title with the API Key (set in the {@code movie.api.url} property)
     * that is needed to be able to actually request the Movie data from the API, and this method specifically contains
     * the {@param additionalInfo} parameter, because it is used by the process that loads the CSV File content, and
     * a way to handle movies that contains swapped data between Nominee and Additional Info columns, is by trying to
     * retrieve the movie with the Additional Info column value after having a negative from the Movie Source external API
     * with the Nominee value
     *
     * @param apiKey {@link String} representing the API Key needed to be able to retrieve data from the API
     * @param movieTitle {@link String} representing the Movie Title to be requested
     * @param additionalInfo {@link String} representing the 'Additional Info' column of the CSV File, because there are
     *                                     movies that the movie name is in the 'Additional Info' and not in the
     *                                     'Nominee' column
     *
     * @return {@link MovieSourceDTO} instance with the returned data from the external Movie Source API
     * (returns {@code null} if the movie is not found)
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    public MovieSourceDTO getMovieSourceDTOFromCSVFile(final String apiKey, final String movieTitle, final String additionalInfo) {
        // In order to use the RestTemplate, it is needed to instantiate a HttpHeader
        final var httpHeaders = new HttpHeaders();
        // The "Accept" header is set informing that it is expected a JSON Payload as a response from the external API
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        // Then the header is set into the HttpEntity
        final var httpEntity = new HttpEntity<>(httpHeaders);
        // Here a Map is built in order to be able to build the get params in the URL using the RestTemplate.exchange method, so it may sanitise the
        // parameter values avoiding possible issues that may occur
        var urlParametersMap = Map.ofEntries(entry(API_KEY_PARAM_NAME, apiKey), entry(MOVIE_TITLE_PARAM_NAME, movieTitle), entry(DATA_TYPE_PARAM_NAME, MOVIE_DATA_TYPE));
        // Below the method dynamically build a String (maybe it could be hardcoded, but while I was coding I ended up making it dynamic) with
        // the parameters and values as placeholders because this is how the Exchange works while it sets the parameter and its respective values
        final var urlParams = buildParamsPlaceholders(urlParametersMap);
        try {
            // Then the exchange is actually called as a GET request with the parameters built by the RestTemplate itself (the "this.movieSourceApiUrl + urlParams" concatenation
            // actually concatenates placeholders and not values, the values will be set by RestTemplate, where they are available in the "urlParametersMap" set below)
            log.trace("Requesting the Movie data from the Movie source API using the following: {}", urlParametersMap.entrySet().stream().map(logMap()).collect(Collectors.joining(", ")));
            var responseEntity = this.restTemplate.exchange(this.movieSourceApiUrl + urlParams, HttpMethod.GET, httpEntity, MovieSourceDTO.class, urlParametersMap);
            // If the server responds with Http Status OK we move forward, otherwise a MovieSourceServiceException is thrown because something went wrong with the request
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // The body is retrieved already deserialized into MovieSourceDTO by the RestTemplate
                var movieSourceDTO = responseEntity.getBody();
                // Below we verify if the variable above is not null (just to be sure to not have a horrible NullPointerException)
                // and verify the "getResponse" if it is also different of Null, again to avoid a NullPointerException
                log.trace("Requested returned as OK");
                if (movieSourceDTO != null && movieSourceDTO.getResponse() != null) {
                    // Bellow we verify the getResponse value returned by the external API. When True means that the
                    // requested movie exists. When False the requested movie does not exist in their system. Using this
                    // field value to verify if the movie exists in their system is cheaper than having to verify the
                    // object fields. Also, there is an "equalsIgnoreCase" being used because the API does not return
                    // exactly what is requested. For instance, if you request a Movie Title as "Metro-Goldwyn-Mayer",
                    // it returns the "The Metro-Goldwyn-Mayer Story" movie
                    if (Boolean.parseBoolean(movieSourceDTO.getResponse()) && movieSourceDTO.getTitle().equalsIgnoreCase(movieTitle)) {
                        // If it is all okay, then the MovieSourceDTO with the Movie data is returned
                        log.trace("Consuming the payload: {}", movieSourceDTO);
                        return movieSourceDTO;
                    } else if (!Boolean.parseBoolean(movieSourceDTO.getResponse()) && additionalInfo != null) {
                        // This weird flow is to attend the scenarios where the Movie name is not in the Nominee column in the CSV file,
                        // and instead it is in the 'Additional Info' column. For this scenario the API returns a Response as FALSE, meaning
                        // that the movie title available in the Nominee does not exist, then the 'additionalInfo' parameter will be used
                        // to try to retrieve the Movie data instead. For that, a new parameter map is instantiated (because the previous instance is
                        // immutable), and a new Movie Title is set in the parameter map setting the Additional Info value
                        urlParametersMap = new HashMap<>(urlParametersMap);
                        urlParametersMap.put(MOVIE_TITLE_PARAM_NAME, additionalInfo);
                        log.info("The movie {} has not been found, trying to load the movie {} available in the 'additionalInfo' field", movieTitle, additionalInfo);
                        // Now another exchange is made using the Additional Info column from the CSV File to see if it exists
                        responseEntity = this.restTemplate.exchange(this.movieSourceApiUrl + urlParams, HttpMethod.GET, httpEntity, MovieSourceDTO.class, urlParametersMap);
                        // Again the Http Status is verified again, where if OK it moves forward
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            // A new deserialized body is retrieved
                            movieSourceDTO = responseEntity.getBody();
                            // Same checks to avoid NullPointerException, and confirming if the Movie returned True meaning that it found a movie, and confirming
                            // that the returned Movie contains exactly the name that was expected, otherwise it returns null
                            if (movieSourceDTO != null && Boolean.parseBoolean(movieSourceDTO.getResponse()) && movieSourceDTO.getTitle().equalsIgnoreCase(additionalInfo)) {
                                log.info("Movie {} from the 'additionalInfo' field successfully retrieved from the API", additionalInfo);
                                // Once everything is ok, the MovieSourceDTO instance is returned
                                return movieSourceDTO;
                            }
                        }
                    }
                }
                // If none of the above ifs are attended, then null is returned because it means that the requested movie does not exist
                // in the external Movie Source API
                return null;
            } else {
                // This block is reached when the RestTemplate gets a HttpStatus response different from 200 of the external Movie Source API,
                // meaning that something went wrong with the request coming from the external service
                throw new MovieSourceServiceException(String.format("The external Movie API returned a non 200 http status that were not expected. It returned: %d: %s", responseEntity.getStatusCode().value(), responseEntity.getStatusCode().name()));
            }
        } catch (final HttpClientErrorException httpClientErrorException) {
            // This block is when something is wrong with the request itself with RestTemplate
            throw new MovieSourceServiceException(String.format("The external API returned an error %d (%s)", httpClientErrorException.getStatusCode().value(), httpClientErrorException.getStatusCode().name()), httpClientErrorException);
        }
    }

    /**
     * This method is used to dynamically build the URL parameters placeholders, based in the {@link Map},
     * returning the parameters and the values as placeholders, to be concatenated in the URL to be set in the
     * {@link RestTemplate#exchange(String, HttpMethod, HttpEntity, Class, Map)} method
     *
     * @param urlParametersMap {@link Map} instance containing the pair-values to be used to build the
     *                                                    get parameters in the URL
     * @return {@link String} representing the parameters to be concatenated in the URL in the GET request with the values
     * as placeholders
     *
     * @author Daniel Chiuratto Seabra
     * @since 05/08/2022
     */
    private String buildParamsPlaceholders(final Map<String, String> urlParametersMap) {
        return "?" + urlParametersMap.keySet().stream().map(s -> String.format("%s={%s}", s, s)).collect(Collectors.joining("&"));
    }

    /**
     * This method returns a lambda used to filter the api key value from the trace log, hidding it
     *
     * @author Daniel Chiuratto Seabra
     * @since 11/08/2022
     */
    private Function<Map.Entry<String, String>, String> logMap() {
        return entry -> String.format("%s: %s", entry.getKey(), API_KEY_PARAM_NAME.equals(entry.getKey()) ? "<HIDDEN>" : entry.getValue());
    }
}
