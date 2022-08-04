package nl.backbase.controller.exception;

public class MovieAPINotFoundException extends RuntimeException {
    public MovieAPINotFoundException(final String message) {
        super(message);
    }
}
