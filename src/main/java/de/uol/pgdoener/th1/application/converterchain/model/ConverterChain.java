package de.uol.pgdoener.th1.application.converterchain.model;

import lombok.Getter;
import lombok.NonNull;

public class ConverterChain {

    @Getter
    private Converter first;
    private Converter last;

    public void add(@NonNull Converter converter) {
        if (this.first == null) {
            this.first = converter;
        } else {
            this.last.setNextHandler(converter);
        }
        this.last = converter;
    }

}
