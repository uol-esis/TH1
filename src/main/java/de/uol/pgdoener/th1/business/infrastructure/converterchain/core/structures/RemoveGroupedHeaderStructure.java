package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record RemoveGroupedHeaderStructure(
        @NonNull Integer[] columns,
        @NonNull Integer[] rows,
        Integer startRow,
        Integer startColumn
) implements IStructure {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RemoveGroupedHeaderStructure that = (RemoveGroupedHeaderStructure) o;
        return Arrays.equals(rows, that.rows) && Objects.equals(startRow, that.startRow) && Arrays.equals(columns, that.columns) && Objects.equals(startColumn, that.startColumn);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(columns);
        result = 31 * result + Arrays.hashCode(rows);
        result = 31 * result + Objects.hashCode(startRow);
        result = 31 * result + Objects.hashCode(startColumn);
        return result;
    }

    @Override
    public String toString() {
        return "RemoveGroupedHeaderStructure{" +
                "columns=" + Arrays.toString(columns) +
                ", rows=" + Arrays.toString(rows) +
                ", startRow=" + startRow +
                ", startColumn=" + startColumn +
                '}';
    }

}
