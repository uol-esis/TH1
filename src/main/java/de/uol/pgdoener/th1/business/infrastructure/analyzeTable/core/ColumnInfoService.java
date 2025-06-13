package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnInfoService {

    private final CellInfoService cellInfoService;

    /**
     * Checks if the given column has more than one type of cells.
     * The header is assumed to be a string and is ignored.
     *
     * @param columnInfo the column to check
     * @return true if there are more than one type, false otherwise
     */
    public boolean hasTypeMismatch(ColumnInfo columnInfo) {
        if (columnInfo.cellInfos().size() < 2) return false;

        List<CellInfo> cellInfos = columnInfo.cellInfos();
        ValueType firstValueType = cellInfos.get(1).valueType();

        for (int i = 2; i < cellInfos.size(); i++) {
            if (cellInfos.get(i).valueType() != firstValueType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the dominant ValueType of a column by skipping the header
     * and returning the first non-empty value type found.
     *
     * @param columnInfo the column to analyze
     * @return the dominant ValueType, or EMPTY if none found
     */
    public ValueType getType(ColumnInfo columnInfo, int dataStart) {
        List<CellInfo> cellInfos = columnInfo.cellInfos();
        // Start at index 1 to skip header
        for (int i = dataStart; i < cellInfos.size(); i++) {
            ValueType type = cellInfos.get(i).valueType();
            if (type == ValueType.EMPTY || type == ValueType.NULL) continue;
            return type;
        }

        return ValueType.EMPTY;
    }

    /**
     * Checks if the given columns are mergeable.
     * Columns are mergeable if only one column in any given row has an entry.
     * The first row is ignored as it should be the header.
     *
     * @param columnInfos the columns to check
     * @return true if the columns are mergeable, false otherwise
     */
    public boolean areMergeable(List<ColumnInfo> columnInfos) {
        if (columnInfos.isEmpty()) {
            return false;
        }

        for (int i = 1; i < columnInfos.getFirst().cellInfos().size(); i++) {
            if (!isRowMergeable(columnInfos, i)) return false;
        }

        return true;
    }

    // TODO check for type compatibility
    private boolean isRowMergeable(List<ColumnInfo> columnInfos, int rowIndex) {
        return columnInfos.stream()
                .filter(c -> !cellInfoService.isEmpty(c.cellInfos().get(rowIndex)))
                .count() == 1;
    }

    public boolean isEmpty(ColumnInfo columnInfo) {
        List<CellInfo> cellInfos = columnInfo.cellInfos();
        return cellInfos.stream()
                .allMatch(cellInfoService::isEmpty);
    }

}
