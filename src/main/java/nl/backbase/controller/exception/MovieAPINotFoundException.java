package nl.backbase.controller.exception;

import nl.backbase.repository.MovieAPIRepository;

/**
 * This {@link Exception} represents the scenario when the requested Movie Title, or does not exist in the {@link MovieAPIRepository}
 * when an Oscar's Best Picture movie title is requested, or when a request is made that requires to reach the external
 * Movie API Service, and it is not found there as well.
 *
 * @author Daniel Chiuratto Seabra
 * @since 01/08/2022
 */
public class MovieAPINotFoundException extends RuntimeException {
    public MovieAPINotFoundException(final String message) {
        super(message);
    }
}
