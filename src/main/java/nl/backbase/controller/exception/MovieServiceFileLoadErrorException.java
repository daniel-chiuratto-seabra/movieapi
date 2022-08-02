package nl.backbase.controller.exception;

import java.io.IOException;

public class MovieServiceFileLoadErrorException extends RuntimeException {
    public MovieServiceFileLoadErrorException(final IOException ioException) {
        super(ioException);
    }
}
