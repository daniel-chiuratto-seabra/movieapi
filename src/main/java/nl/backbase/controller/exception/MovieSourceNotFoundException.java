package nl.backbase.controller.exception;

public class MovieSourceNotFoundException extends RuntimeException {
    public MovieSourceNotFoundException(final String message) {
        super(message);
    }
}
