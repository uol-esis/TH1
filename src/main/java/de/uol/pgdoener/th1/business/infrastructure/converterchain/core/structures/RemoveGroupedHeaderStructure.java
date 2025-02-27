package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

public record RemoveGroupedHeaderStructure(
        Integer[] columns,
        Integer[] rows,
        Integer startRow,
        Integer startColumn
) implements IStructure {
}
