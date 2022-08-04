package nl.backbase.controller.exception;

public class MovieAPISourceServiceException extends RuntimeException {
    public MovieAPISourceServiceException(final String message) {
        super(message);
    }

    public MovieAPISourceServiceException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}

