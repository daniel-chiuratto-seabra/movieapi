package nl.backbase.controller.exception;

import nl.backbase.repository.MovieRepository;

/**
 * This {@link Exception} represents the scenario when the requested Movie Title, or does not exist in the {@link MovieRepository}
 * when an Oscar's Best Picture movie title is requested, or when a request is made that requires to reach the external
 * Movie API Service, and it is not found there as well.
 *
 * @author Daniel Chiuratto Seabra
 * @since 01/08/2022
 */
public class MovieNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6195255635762594121L;

	public MovieNotFoundException(final String message) {
        super(message);
    }
}
