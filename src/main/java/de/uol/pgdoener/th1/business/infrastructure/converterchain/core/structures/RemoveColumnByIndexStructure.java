package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

public record RemoveColumnByIndexStructure(
        Integer[] columns
) implements IStructure {
}