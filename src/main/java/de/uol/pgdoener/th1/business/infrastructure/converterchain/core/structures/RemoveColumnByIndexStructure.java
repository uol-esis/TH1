package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import lombok.NonNull;

import java.util.Arrays;

public record RemoveColumnByIndexStructure(
        @NonNull Integer[] columns
) implements IStructure {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RemoveColumnByIndexStructure that = (RemoveColumnByIndexStructure) o;
        return Arrays.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(columns);
    }

    @Override
    public String toString() {
        return "RemoveColumnByIndexStructure{" +
                "columns=" + Arrays.toString(columns) +
                '}';
    }

}