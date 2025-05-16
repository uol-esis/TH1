package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MatrixInfoService {

    final RowInfoService rowInfoService;

    /**
     * Calculates the maximum number of columns across all rows.
     */
    public List<Integer> getHeaderRows(MatrixInfo matrixInfo) {
        List<Integer> headerRows = new ArrayList<>();
        List<RowInfo> rowInfos = matrixInfo.rowInfos();
        for (int i = 0; i < rowInfos.size(); i++) {
            RowInfo rowInfo = rowInfos.get(i);
            if (rowInfoService.isHeaderRow(rowInfo)) {
                headerRows.add(i);
            } else if (!headerRows.isEmpty()) {
                break;
            }
        }
        return headerRows;
    }

    public List<Integer> checkTypeMismatch(MatrixInfo matrixInfo) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        List<Integer> typeMismatches = getHeaderRows(matrixInfo);
        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo.cellInfos().size() < 2) continue;

            List<CellInfo> cellInfos = columnInfo.cellInfos();
            ValueType firstValueType = cellInfos.get(1).valueType();

            for (int i = 2; i < cellInfos.size(); i++) {
                if (cellInfos.get(i).valueType() != firstValueType) {
                    typeMismatches.add(columnInfo.columnIndex());
                }
            }
        }
        return typeMismatches;
    }

    /**
     * Checks whether the table header should be considered grouped.
     */
    public boolean hasGroupedHeader(MatrixInfo matrixInfo) {
        List<Integer> rowHeader = getHeaderRows(matrixInfo);
        return rowHeader.size() > 1;
    }

    /**
     * Calculates the maximum number of columns across all rows.
     */
    public int getMaxRowSize(MatrixInfo matrixInfo) {
        int maxRowSize = 0;
        List<RowInfo> rowInfos = matrixInfo.rowInfos();
        for (RowInfo rowInfo : rowInfos) {
            List<CellInfo> cellInfos = rowInfo.cellInfos();
            int rowSize = rowInfoService.getFilledPositionsSize(cellInfos);
            maxRowSize = Math.max(maxRowSize, rowSize);
        }
        return maxRowSize;
    }

    /**
     * Returns a list of row IDs that are partially filled but not complete.
     */
    public List<Integer> getRowToFill(MatrixInfo matrixInfo) {
        List<Integer> result = new ArrayList<>();
        int maxRowSize = getMaxRowSize(matrixInfo);
        List<RowInfo> rowInfos = matrixInfo.rowInfos();

        for (RowInfo rowInfo : rowInfos) {
            if (rowInfoService.hasRowToFill(maxRowSize, rowInfo.cellInfos())) {
                result.add(rowInfo.rowId());
            }
        }
        return result;
    }

    /**
     * Checks if there are any rows that are incomplete (partially filled).
     */
    public boolean hasEmptyRow(MatrixInfo matrixInfo) {
        return !getRowToFill(matrixInfo).isEmpty();
    }

    /**
     * Identifies column indexes where only one row has an entry.
     * Note: currently simplified to always return 0.
     */
    public List<Integer> getColumnIndexes(MatrixInfo matrixInfo) {
        List<Integer> rowIndexes = new ArrayList<>();
        List<RowInfo> rowInfos = matrixInfo.rowInfos();

        for (RowInfo rowInfo : rowInfos) {
            /// TODO: überarbeiten wenn mehr als eine spalte in der column aufgelöst werden muss.
            rowInfoService.countEntries(rowInfo);
            if (rowInfo.countEntries() == 1) {
                rowIndexes.add(0);
            }
        }
        return rowIndexes;
    }

    /**
     * Builds the header names for the table including a final "Anzahl" column.
     * Rotates the last element to the beginning.
     */
    public List<String> getHeaderNames() {
        List<String> headerNames = new ArrayList<>(rowInfos.stream().map(RowInfo::getHeaderName).toList());

        if (!headerNames.isEmpty()) {
            String lastElement = headerNames.removeLast();
            headerNames.addFirst(lastElement); // An den Anfang setzen
        }
        headerNames.add("Anzahl");
        return headerNames;
    }
}
