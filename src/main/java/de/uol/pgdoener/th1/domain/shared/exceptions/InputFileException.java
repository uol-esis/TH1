package de.uol.pgdoener.th1.domain.shared.exceptions;

import java.io.IOException;

/**
 * This exception is thrown when the input file cannot be read.
 */
public class InputFileException extends RuntimeException {

    public InputFileException(String message) {
        super(message);
    }

    public InputFileException(String message, IOException cause) {
        super(message, cause);
    }

}
