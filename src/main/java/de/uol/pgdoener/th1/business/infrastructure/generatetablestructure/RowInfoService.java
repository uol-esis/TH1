package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RowInfoService {

    private final CellInfoService cellInfoService;

    /**
     * Checks if this row is a valid Header.
     * A valid header is either a string or is empty.
     *
     * @param rowInfo .
     * @return a boolean if the row is a headerRow.
     */
    public boolean isHeaderRow(RowInfo rowInfo) {
        List<CellInfo> cellInfos = rowInfo.cellInfos();
        int strings = 0;
        for (CellInfo cellInfo : cellInfos) {
            if (!cellInfoService.isEmpty(cellInfo) && !cellInfoService.isString(cellInfo)) {
                return false;
            }
            if (cellInfoService.isString(cellInfo)) {
                strings++;
            }
        }
        return strings >= 1;
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

    /**
     * Checks if this row has a row to Fill.
     * A row to fill is a roe with only one valid entry.
     *
     * @param rowInfos .
     * @return a list of RowInfo.
     */
    public List<RowInfo> getRowsToFill(List<RowInfo> rowInfos) {
        List<RowInfo> rowsToFill = new ArrayList<>();
        for (RowInfo rowInfo : rowInfos) {

            if (isRowToFill(rowInfo)) {
                rowsToFill.add(rowInfo);
            } else {
                break;
            }
        }

        return rowsToFill;
    }

    public List<Integer> getGroupHeaderIndex(List<RowInfo> rowInfos) {
        List<Integer> headerRowIndex = new ArrayList<>();
        for (RowInfo rowInfo : rowInfos) {
            if (headerRowIndex.isEmpty()) {
                headerRowIndex.add(rowInfo.rowId());
                continue;
            }

            int maxFilledPositions = rowInfo.cellInfos().size();
            int filledPositions = getFilledPositionsSize(rowInfo);
            if (filledPositions == maxFilledPositions) {
                headerRowIndex.add(rowInfo.rowId());
            }

            return headerRowIndex;
        }

        return headerRowIndex;
    }

    public List<ColumnInfo> getColumnsToFill(List<ColumnInfo> columnInfos) {
        List<ColumnInfo> columnsToFill = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfos) {

            if (hasColumnToFill(columnInfo)) {
                columnsToFill.add(columnInfo);
            } else if (!columnsToFill.isEmpty()) {
                break;
            }
        }

        return columnsToFill;
    }

    /**
     * Checks if this row is partially filled but not complete.
     *
     * @return true if the row is neither empty nor complete.
     */
    public boolean isRowToFill(RowInfo rowInfo) {
        int filledPositionsSize = getFilledPositionsSize(rowInfo);

        return filledPositionsSize < rowInfo.cellInfos().size();
    }

    public boolean hasColumnToFill(ColumnInfo columnInfo) {
        int filledPositionsSize = getFilledPositionsSize(columnInfo);

        return filledPositionsSize <= columnInfo.cellInfos().size();
    }

    /**
     * Returns a list of column indexes that are empty.
     */
    public int getFilledPositionsSize(RowInfo rowInfo) {
        List<CellInfo> cellInfos = rowInfo.cellInfos();
        return (int) cellInfos.stream()
                .filter(cellInfoService::hasEntry)
                .count();
    }

    public int getFilledPositionsSize(ColumnInfo columnInfo) {
        List<CellInfo> cellInfos = columnInfo.cellInfos();
        return (int) cellInfos.stream()
                .filter(cellInfoService::hasEntry)
                .count();
    }


    ///**
    // * Adds a cell to this row.
    // *
    // * @param cellInfo the CellInfo object to be added.
    // */
    //public void addColumnInfo(CellInfo cellInfo) {
    //    cellInfos.add(cellInfo);
    //    log.debug("Added cell with column ID {} to row {}", cellInfo.getColumnId(), rowId);
    //}

    /**
     * Counts how many cells in the row have entries.
     */
    public int countEntries(RowInfo rowInfo) {
        List<CellInfo> cellInfos = rowInfo.cellInfos();
        return (int) cellInfos.stream()
                .filter(cellInfoService::hasEntry).count();
    }

    /**
     * Returns a list of column indexes that are filled.
     */
    //public List<Integer> getEmptyPositions(RowInfo rowInfo) {
    //    List<CellInfo> cellInfos = rowInfo.cellInfos();
    //    return cellInfos.stream()
    //            .filter(c -> !cellInfoService.hasEntry(c))
    //            .toList();
    //}

    /**
     * Returns the total number of cells in this row.
     */
    //public List<Integer> getFilledPositions() {
    //    return cellInfos.stream()
    //            .filter(CellInfo::hasEntry)
    //            .map(CellInfo::getColumnId)
    //            .toList();
    //}
}
