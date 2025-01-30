package de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures;

public record RemoveColumnByIndexStructure(
        Integer[] columns
) implements IStructure {
}