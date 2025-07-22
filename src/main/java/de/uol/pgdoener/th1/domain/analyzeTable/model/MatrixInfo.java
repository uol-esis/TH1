package de.uol.pgdoener.th1.domain.analyzeTable.model;

import java.util.List;

/**
 * Represents a matrix structure composed of multiple rows (RowInfo).
 * Provides utilities for validation and structure analysis.
 */
public record MatrixInfo(
        List<RowInfo> rowInfos,
        List<ColumnInfo> columnInfos
) {
}
