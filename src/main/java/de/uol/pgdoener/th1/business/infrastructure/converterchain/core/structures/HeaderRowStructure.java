package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import lombok.NonNull;

import java.util.Arrays;

public record HeaderRowStructure(
        @NonNull String[] headerRows
) implements IStructure {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        HeaderRowStructure that = (HeaderRowStructure) o;
        return Arrays.equals(headerRows, that.headerRows);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(headerRows);
    }

    @Override
    public String toString() {
        return "HeaderRowStructure{" +
                "headerRows=" + Arrays.toString(headerRows) +
                '}';
    }

}
