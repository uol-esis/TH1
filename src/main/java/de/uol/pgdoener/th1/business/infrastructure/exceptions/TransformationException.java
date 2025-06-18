package de.uol.pgdoener.th1.business.infrastructure.exceptions;

public class TransformationException extends RuntimeException {

    public TransformationException(String message) {
        super(message);
    }

    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }

}
