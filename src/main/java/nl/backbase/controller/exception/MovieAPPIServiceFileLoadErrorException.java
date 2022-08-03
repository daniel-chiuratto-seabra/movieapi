package nl.backbase.controller.exception;

import java.io.IOException;

public class MovieAPPIServiceFileLoadErrorException extends RuntimeException {
    public MovieAPPIServiceFileLoadErrorException(final IOException ioException) {
        super(ioException);
    }
}
