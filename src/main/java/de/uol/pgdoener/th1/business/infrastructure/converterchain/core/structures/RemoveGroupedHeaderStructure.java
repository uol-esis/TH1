package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

public record RemoveGroupedHeaderStructure(
        Integer[] columns,
        Integer[] rows,
        int startRow,
        int endRow,
        int startColumn,
        int endColumn
) implements IStructure {
}
