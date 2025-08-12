package de.uol.pgdoener.th1.domain.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final Object details;
    private final String suggestion;

    public ServiceException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null, null);
    }

    public ServiceException(String message, HttpStatus httpStatus, Object details) {
        this(message, httpStatus, details, null);
    }

    public ServiceException(String message, HttpStatus httpStatus, Object details, String suggestion) {
        super(message);
        this.httpStatus = httpStatus;
        this.details = details;
        this.suggestion = suggestion;
    }
}
