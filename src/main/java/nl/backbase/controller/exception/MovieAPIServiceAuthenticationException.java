package nl.backbase.controller.exception;

public class MovieAPIServiceAuthenticationException extends RuntimeException {
    public MovieAPIServiceAuthenticationException(final Throwable throwable) {
        super(throwable);
    }
}
