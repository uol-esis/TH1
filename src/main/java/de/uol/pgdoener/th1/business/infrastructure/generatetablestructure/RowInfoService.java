package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return strings > 1;
    }

    /**
     * Checks if this row is partially filled but not complete.
     *
     * @return true if the row is neither empty nor complete.
     */
    public boolean hasRowToFill(RowInfo rowInfo) {
        List<CellInfo> cellInfos = rowInfo.cellInfos();

        int filledPositionsSize = getFilledPositionsSize(cellInfos);

        return filledPositionsSize <= cellInfos.size();
    }

    /**
     * Returns a list of column indexes that are empty.
     */
    public int getFilledPositionsSize(List<CellInfo> cellInfos) {
        return (int) cellInfos.stream()
                .filter(cellInfoService::hasEntry)
                .count();
    }

    /**
     * Adds a cell to this row.
     *
     * @param cellInfo the CellInfo object to be added.
     */
    public void addColumnInfo(CellInfo cellInfo) {
        cellInfos.add(cellInfo);
        log.debug("Added cell with column ID {} to row {}", cellInfo.getColumnId(), rowId);
    }

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
    public List<Integer> getEmptyPositions() {
        return cellInfos.stream()
                .filter(c -> !c.hasEntry())
                .map(CellInfo::getColumnId)
                .toList();
    }

    /**
     * Returns the total number of cells in this row.
     */
    public List<Integer> getFilledPositions() {
        return cellInfos.stream()
                .filter(CellInfo::hasEntry)
                .map(CellInfo::getColumnId)
                .toList();
    }
}
