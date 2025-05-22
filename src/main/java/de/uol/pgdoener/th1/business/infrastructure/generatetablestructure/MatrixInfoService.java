package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatrixInfoService {

    private final RowInfoService rowInfoService;
    private final ColumnInfoService columnInfoService;
    private final CellInfoService cellInfoService;

    /**
     * Returns a list of indices for header rows.
     * A header row is defined as a row that does not contain any data, but describes the data in the rows below it.
     *
     * @param matrixInfo The matrix information
     * @return A list of indices for header rows
     */
    public List<Integer> getHeaderRowIndices(MatrixInfo matrixInfo) {
        return getHeaderRowInfos(matrixInfo).stream()
                .map(RowInfo::rowIndex)
                .toList();
    }

    /**
     * Returns a list of header rows.
     * A header row is defined as a row that does not contain any data, but describes the data in the rows below it.
     *
     * @param matrixInfo The matrix information
     * @return A list of header rows
     */
    public List<RowInfo> getHeaderRowInfos(MatrixInfo matrixInfo) {
        List<RowInfo> headerRows = new ArrayList<>();
        List<RowInfo> rowInfos = matrixInfo.rowInfos();

        for (RowInfo rowInfo : rowInfos) {
            if (rowInfoService.isHeaderRow(rowInfo)) {
                headerRows.add(rowInfo);
            } else if (!headerRows.isEmpty()) {
                break;
            }
        }
        return headerRows;
    }

    /**
     * Returns a list of header columns.
     * A header column is defined as a column that does not contain any data, but describes the data in the columns to
     * the right of it.
     *
     * @param matrixInfo The matrix information
     * @return A list of header columns
     */
    public List<ColumnInfo> getHeaderColumnInfos(MatrixInfo matrixInfo) {
        List<ColumnInfo> headerColumns = new ArrayList<>();
        List<ColumnInfo> colInfos = matrixInfo.columnInfos();

        for (ColumnInfo colInfo : colInfos) {
            if (columnInfoService.isHeaderCol(colInfo)) {
                headerColumns.add(colInfo);
            } else if (!headerColumns.isEmpty()) {
                break;
            }
        }
        return headerColumns;
    }

    /**
     * Calculates the maximum number of columns across all rows.
     */
    public int getFirstDataRowIndex(MatrixInfo matrixInfo, int index) {
        List<RowInfo> rowInfos = matrixInfo.rowInfos();
        int startIndex = index;

        for (int i = index; i < rowInfos.size(); i++) {
            RowInfo rowInfo = rowInfos.get(i);
            int filledPositions = rowInfoService.getFilledPositionsSize(rowInfo);

            // check if data Row ?
            if (filledPositions > 0) {
                return i;
            }

            startIndex = i;
        }
        return startIndex;
    }

    public int getFirstDataColumnIndex(MatrixInfo matrixInfo, int index) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        int startIndex = index;

        for (int i = index; i < columnInfos.size(); i++) {
            ColumnInfo columnInfo = columnInfos.get(i);
            int filledPositions = columnInfoService.getFilledPositionsSize(columnInfo);

            // check if data Row ?
            if (filledPositions > 0) {
                break;
            }

            startIndex = i;
        }
        return startIndex;
    }

    /**
     * Checks whether the table header should be considered grouped.
     */
    public boolean hasGroupedHeader(MatrixInfo matrixInfo) {
        List<Integer> rowHeader = getHeaderRowIndices(matrixInfo);
        return rowHeader.size() > 1;
    }

    /**
     * Returns a list of row IDs that are partially filled but not complete.
     */
    public List<Integer> getRowToFill(MatrixInfo matrixInfo) {
        List<Integer> result = new ArrayList<>();
        List<RowInfo> rowInfos = matrixInfo.rowInfos();

        for (RowInfo rowInfo : rowInfos) {
            if (rowInfoService.hasEmptyCellsStartingFrom(rowInfo, 0)) {
                result.add(rowInfo.rowIndex());
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
     * This method detects the rectangle of a grouped header in the top left corner of the matrix.
     * <p>
     * A grouped header rectangle has entries in the first column.
     * To the right there are empty cells until the data starts.
     * So each row in the rectangle has exactly one filled cell in the first column.
     * Below the rectangle there are the headers for the grouped header columns.
     * To the right of those there are only empty cells.
     *
     * @param matrixInfo the matrix to analyze
     * @return the rectangle of the grouped header
     */
    public Optional<Pair<Integer, Integer>> detectGroupedHeaderCorner(MatrixInfo matrixInfo) {
        if (!cellInfoService.hasEntry(matrixInfo.rowInfos().getFirst().cellInfos().getFirst())) {
            // no entry in cell 0,0 => no grouped header
            return Optional.empty();
        }

        // find width of rectangle
        int width = 1;
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        for (int i = 1; i < columnInfos.size(); i++) {
            ColumnInfo columnInfo = columnInfos.get(i);
            CellInfo cellInfo = columnInfo.cellInfos().getFirst();
            if (!cellInfoService.hasEntry(cellInfo)) {
                width++;
            } else {
                break;
            }
        }

        // find height of rectangle
        int height = 1;
        List<RowInfo> rowInfos = matrixInfo.rowInfos();
        for (int i = 1; i < rowInfos.size(); i++) {
            RowInfo rowInfo = rowInfos.get(i);
            CellInfo leftCell = rowInfo.cellInfos().getFirst();
            CellInfo firstHeaderCell = rowInfo.cellInfos().get(width);
            if (cellInfoService.hasEntry(leftCell) && cellInfoService.hasEntry(firstHeaderCell)) {
                height++;
            } else {
                break;
            }
        }
        if (height > rowInfos.size() / 2) {
            // unrealistic height
            return Optional.empty();
        }

        // check if the rectangle is valid
        for (int i = 0; i < height; i++) {
            if (cellInfoService.isEmpty(matrixInfo.rowInfos().get(i).cellInfos().getFirst())) {
                return Optional.empty();
            }
            for (int j = 1; j < width; j++) {
                if (cellInfoService.hasEntry(matrixInfo.rowInfos().get(i).cellInfos().get(j))) {
                    return Optional.empty();
                }
            }
        }

        // check if there is a column header row
        RowInfo columnHeaderRow = matrixInfo.rowInfos().get(height);
        for (int i = 0; i < width; i++)
            if (cellInfoService.isEmpty(columnHeaderRow.cellInfos().get(i)))
                return Optional.empty();
        for (int i = width; i < columnHeaderRow.cellInfos().size(); i++)
            if (cellInfoService.hasEntry(columnHeaderRow.cellInfos().get(i)))
                return Optional.empty();

        // if there is more than 1 row in the rectangle, check if the top rows have empty cells
        if (height >= 2) {
            boolean hasEmptyCells = false;
            for (int i = 0; i < height - 1; i++) {
                if (rowInfoService.hasEmptyCellsStartingFrom(matrixInfo.rowInfos().get(i), width)) {
                    hasEmptyCells = true;
                    break;
                }
            }
            if (!hasEmptyCells) {
                return Optional.empty();
            }
        }

        return Optional.of(Pair.of(width, height));
    }

//    /**
//     * Identifies column indexes where only one row has an entry.
//     * Note: currently simplified to always return 0.
//     */
//    public List<Integer> getColumnIndexes(MatrixInfo matrixInfo) {
//        List<Integer> rowIndexes = new ArrayList<>();
//        List<RowInfo> rowInfos = matrixInfo.rowInfos();
//
//        for (RowInfo rowInfo : rowInfos) {
//            /// TODO: überarbeiten wenn mehr als eine spalte in der column aufgelöst werden muss.
//            rowInfoService.countEntries(rowInfo);
//            if (rowInfo.countEntries() == 1) {
//                rowIndexes.add(0);
//            }
//        }
//        return rowIndexes;
//    }
//
//    /**
//     * Builds the header names for the table including a final "Anzahl" column.
//     * Rotates the last element to the beginning.
//     */
//    public List<String> getHeaderNames() {
//        List<String> headerNames = new ArrayList<>(rowInfos.stream().map(RowInfo::getHeaderName).toList());
//
//        if (!headerNames.isEmpty()) {
//            String lastElement = headerNames.removeLast();
//            headerNames.addFirst(lastElement); // An den Anfang setzen
//        }
//        headerNames.add("Anzahl");
//        return headerNames;
//    }
}
