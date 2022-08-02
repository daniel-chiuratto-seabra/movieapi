package nl.backbase.controller.exception;

public class MovieServiceAuthenticationException extends RuntimeException {
    public MovieServiceAuthenticationException(final Throwable throwable) {
        super(throwable);
    }
}
