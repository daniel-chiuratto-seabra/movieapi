package nl.backbase.controller.exception;

public class MovieAPISourceNotFoundException extends RuntimeException {
    public MovieAPISourceNotFoundException(final String message) {
        super(message);
    }
}
