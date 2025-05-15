package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import java.util.List;

public record ColumnInfo(
        int columnIndex,
        List<CellInfo> cellInfos
) {
}
