package de.uol.pgdoener.th1.application.analyzeTable.model;

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

    public boolean isEmpty(RowInfo rowInfo) {
        List<CellInfo> cellInfos = rowInfo.cellInfos();
        return cellInfos.stream()
                .allMatch(cellInfoService::isEmpty);
    }

    public List<Integer> getEmptyPositions(RowInfo firstRow) {
        List<CellInfo> cellInfos = firstRow.cellInfos();
        return cellInfos.stream()
                .filter(cellInfoService::isEmpty)
                .map(CellInfo::columnIndex)
                .toList();
    }

}
