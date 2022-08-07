package nl.backbase.controller.exception;

import org.springframework.web.client.RestTemplate;

/**
 * This {@link Exception} represents when an {@link Exception} is thrown by the {@link RestTemplate}, during a request from
 * the external Movie API Service
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
public class MovieAPISourceServiceException extends RuntimeException {
    public MovieAPISourceServiceException(final String message) {
        super(message);
    }

    public MovieAPISourceServiceException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}

