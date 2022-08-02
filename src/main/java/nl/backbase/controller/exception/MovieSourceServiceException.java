package nl.backbase.controller.exception;

public class MovieSourceServiceException extends RuntimeException {
    public MovieSourceServiceException(final String message) {
        super(message);
    }
}

