package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

public abstract class Converter {
    protected Converter nextConverter;

    public String[][] handleRequest(String[][] matrix) {
        if (nextConverter != null) {
            return nextConverter.handleRequest(matrix);
        }
        return matrix;
    }

    public void setNextHandler(Converter nextConverter) {
        this.nextConverter = nextConverter;
    }
}