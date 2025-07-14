package de.uol.pgdoener.th1.application.converterchain.exception;

import lombok.Getter;

@Getter
public class ConverterException extends RuntimeException {

    private final int converterIndex;

    public ConverterException(int converterIndex, String message) {
        super(message);
        this.converterIndex = converterIndex;
    }

}
