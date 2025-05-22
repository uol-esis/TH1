package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ValueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public boolean isHeaderCol(ColumnInfo colInfo) {
        List<CellInfo> cellInfos = colInfo.cellInfos();
        int strings = 0;
        for (CellInfo cellInfo : cellInfos) {
            if (!cellInfoService.isEmpty(cellInfo) && !cellInfoService.isString(cellInfo)) {
                return false;
            }
            if (cellInfoService.isString(cellInfo)) {
                strings++;
            }
        }
        return strings > 1;
    }

    public boolean hasEmptyCellsStartingFrom(ColumnInfo columnInfo, int startRow) {
        List<CellInfo> cellInfos = columnInfo.cellInfos();

        for (int i = startRow; i < cellInfos.size(); i++) {
            CellInfo cellInfo = cellInfos.get(i);
            if (!cellInfoService.hasEntry(cellInfo)) {
                return true;
            }
        }

        return false;
    }

    public List<ColumnInfo> getColumnsToFill(List<ColumnInfo> columnInfos, int startRow) {
        List<ColumnInfo> columnsToFill = new ArrayList<>();

        for (ColumnInfo columnInfo : columnInfos) {
            if (hasEmptyCellsStartingFrom(columnInfo, startRow)) {
                columnsToFill.add(columnInfo);
            } else {
                break;
            }
        }

        return columnsToFill;
    }

    public int getFilledPositionsSize(ColumnInfo columnInfo) {
        List<CellInfo> cellInfos = columnInfo.cellInfos();
        return (int) cellInfos.stream()
                .filter(cellInfoService::hasEntry)
                .count();
    }

}
