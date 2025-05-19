package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
public class MatrixInfoFactory {

    public MatrixInfo createParallel(String[][] matrix) {
        CellInfo[][] columnCellInfos = new CellInfo[matrix[0].length][matrix.length];

        final List<RowInfo> rowInfos = IntStream.range(0, matrix.length)
                .parallel()
                .mapToObj(rowIndex -> {
                    List<CellInfo> rowCells = new ArrayList<>(matrix[rowIndex].length);

                    for (int columnIndex = 0; columnIndex < matrix[rowIndex].length; columnIndex++) {
                        CellInfo cellInfo = createCell(rowIndex, columnIndex, matrix[rowIndex][columnIndex]);
                        rowCells.add(cellInfo);
                        columnCellInfos[columnIndex][rowIndex] = cellInfo;
                    }

                    return new RowInfo(rowIndex, rowCells);
                })
                .toList();

        final List<ColumnInfo> columnInfos = new ArrayList<>(matrix[0].length);
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            columnInfos.add(new ColumnInfo(columnIndex, Arrays.asList(columnCellInfos[columnIndex])));
        }

        return new MatrixInfo(rowInfos, columnInfos);
    }

    public MatrixInfo createFast(String[][] matrix) {
        final List<RowInfo> rowInfos = new ArrayList<>(matrix.length);
        CellInfo[][] columnCellInfos = new CellInfo[matrix[0].length][matrix.length];

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            List<CellInfo> rowCells = new ArrayList<>(matrix[rowIndex].length);

            for (int columnIndex = 0; columnIndex < matrix[rowIndex].length; columnIndex++) {
                CellInfo cellInfo = createCell(rowIndex, columnIndex, matrix[rowIndex][columnIndex]);
                rowCells.add(cellInfo);
                columnCellInfos[columnIndex][rowIndex] = cellInfo;
            }

            rowInfos.add(new RowInfo(rowIndex, rowCells));
        }

        final List<ColumnInfo> columnInfos = new ArrayList<>(matrix[0].length);
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            columnInfos.add(new ColumnInfo(columnIndex, Arrays.asList(columnCellInfos[columnIndex])));
        }

        return new MatrixInfo(rowInfos, columnInfos);
    }

    /**
     * Extracts metadata from the top rows of the matrix until the data row begins.
     *
     * @param matrix the raw matrix read from the file
     * @return MatrixInfo object with structure metadata
     */
    public MatrixInfo create(String[][] matrix) {
        List<RowInfo> rowInfos = new ArrayList<>(matrix.length);

        for (int i = 0; i < matrix.length; i++) {
            RowInfo rowInfo = createRow(i, matrix[i]);
            rowInfos.add(rowInfo);
        }

        List<ColumnInfo> columnInfos = createColumnInfos(rowInfos);

        return new MatrixInfo(rowInfos, columnInfos);
    }

    private List<ColumnInfo> createColumnInfos(List<RowInfo> rowInfos) {
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < rowInfos.getFirst().cellInfos().size(); columnIndex++) {
            List<CellInfo> cellInfos = new ArrayList<>();

            //noinspection ForLoopReplaceableByForEach
            for (int rowIndex = 0; rowIndex < rowInfos.size(); rowIndex++) {
                CellInfo cellInfo = rowInfos.get(rowIndex).cellInfos().get(columnIndex);
                cellInfos.add(cellInfo);
            }

            ColumnInfo columnInfo = new ColumnInfo(columnIndex, cellInfos);
            columnInfos.add(columnInfo);
        }
        return columnInfos;
    }

    private RowInfo createRow(int rowIndex, String[] row) {
        List<CellInfo> cellInfos = new ArrayList<>(row.length);

        for (int i = 0; i < row.length; i++) {
            CellInfo cellInfo = createCell(rowIndex, i, row[i]);
            cellInfos.add(cellInfo);
        }

        return new RowInfo(rowIndex, cellInfos);
    }

    private CellInfo createCell(int rowIndex, int colIndex, String entry) {
        ValueType valueType = detectType(entry);
        return new CellInfo(rowIndex, colIndex, entry, valueType);
    }

    private ValueType detectType(String entry) {

        if (entry == null) return ValueType.NULL;
        if (entry.isBlank()) return ValueType.EMPTY;
        if (isInteger(entry)) return ValueType.INTEGER;
        if (isDouble(entry)) return ValueType.DOUBLE;
        if (isBoolean(entry)) return ValueType.BOOLEAN;
        if (isCharacter(entry)) return ValueType.CHARACTER;

        return ValueType.STRING;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    private boolean isCharacter(String s) {
        return s.length() == 1;
    }

}
