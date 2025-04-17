package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import lombok.NonNull;

import java.util.Arrays;

public record RemoveRowByIndexStructure(
        @NonNull Integer[] rows
) implements IStructure {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RemoveRowByIndexStructure that = (RemoveRowByIndexStructure) o;
        return Arrays.equals(rows, that.rows);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rows);
    }

    @Override
    public String toString() {
        return "RemoveRowByIndexStructure{" +
                "rows=" + Arrays.toString(rows) +
                '}';
    }

}
