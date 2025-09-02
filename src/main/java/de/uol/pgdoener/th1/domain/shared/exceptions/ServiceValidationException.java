package de.uol.pgdoener.th1.domain.shared.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ServiceValidationException extends RuntimeException {
    private final String filterField;
    private final String details;
    private final HttpStatus httpStatus;

    public ServiceValidationException(String message, String filterField, String details, HttpStatus httpStatus) {
        super(message);
        this.filterField = filterField;
        this.details = details;
        this.httpStatus = httpStatus;
    }

}