package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import lombok.Getter;

public class ConverterChain {

    @Getter
    private Converter first;
    private Converter last;

    public void add(Converter converter) {
        if (this.first == null) {
            this.first = converter;
        } else {
            this.last.setNextHandler(converter);
        }
        this.last = converter;
    }

}
