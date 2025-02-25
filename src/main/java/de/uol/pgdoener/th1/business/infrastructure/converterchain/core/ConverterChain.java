package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

public class ConverterChain {
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

    public Converter getFirst() {
        return this.first;
    }
}

