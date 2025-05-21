package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
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
     * @param rowInfo the RowInfo object to be checked
     * @return a boolean if the row is a headerRow
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

    /**
     * Checks if this row has a row to Fill.
     * A row to fill is a row with only one valid entry.
     *
     * @param rowInfos    the list of RowInfo objects to be checked
     * @param startColumn the column index to start checking from
     * @return a list of RowInfos that have empty cells
     */
    public List<RowInfo> getRowsToFill(List<RowInfo> rowInfos, int startColumn) {
        List<RowInfo> rowsToFill = new ArrayList<>();

        for (RowInfo rowInfo : rowInfos) {
            if (hasEmptyCellsStartingFrom(rowInfo, startColumn)) {
                rowsToFill.add(rowInfo);
            } else {
                break;
            }
        }

        return rowsToFill;
    }

    public List<Integer> getGroupHeaderIndex(List<RowInfo> rowInfos) {
        List<Integer> headerRowIndex = new ArrayList<>();
        boolean hasOneFilledRow = false;
        for (RowInfo rowInfo : rowInfos) {
            int maxFilledPositions = rowInfo.cellInfos().size();
            int filledPositions = getFilledPositionsSize(rowInfo);
            boolean isFullyFilled = (filledPositions == maxFilledPositions);

            if (!hasOneFilledRow) {
                headerRowIndex.add(rowInfo.rowIndex());

                if (isFullyFilled) {
                    hasOneFilledRow = true;
                }
            } else {
                if (!isFullyFilled) {
                    break;
                }
                headerRowIndex.add(rowInfo.rowIndex());
            }
        }
        return headerRowIndex;
    }

    /**
     * Checks if this row is partially filled but not complete.
     *
     * @return true if the row is neither empty nor complete.
     */
    public boolean hasEmptyCellsStartingFrom(RowInfo rowInfo, int startColumn) {
        List<CellInfo> cellInfos = rowInfo.cellInfos();

        for (int i = startColumn; i < cellInfos.size(); i++) {
            CellInfo cellInfo = cellInfos.get(i);
            if (!cellInfoService.hasEntry(cellInfo)) {
                return true;
            }
        }

        return false;
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

    ///**
    // * Adds a cell to this row.
    // *
    // * @param cellInfo the CellInfo object to be added.
    // */
    //public void addColumnInfo(CellInfo cellInfo) {
    //    cellInfos.add(cellInfo);
    //    log.debug("Added cell with column ID {} to row {}", cellInfo.getColumnId(), rowIndex);
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
