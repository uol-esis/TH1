package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import java.util.List;

/**
 * Represents a row in the matrix, containing cell information and an optional header.
 */
public record RowInfo(
        int rowIndex,
        List<CellInfo> cellInfos
) {
}