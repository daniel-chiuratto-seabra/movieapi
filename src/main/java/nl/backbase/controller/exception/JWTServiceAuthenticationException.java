package nl.backbase.controller.exception;

public class JWTServiceAuthenticationException extends RuntimeException {
    public JWTServiceAuthenticationException(final Exception throwable) {
        super(throwable);
    }
}
