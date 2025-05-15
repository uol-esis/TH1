package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MatrixInfoFactory {

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

        List<ColumnInfo> columnInfos = new ArrayList<>();
        // TODO handle rows with different length
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

        return new MatrixInfo(rowInfos, columnInfos);
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
        return new CellInfo(rowIndex, colIndex, entry);
    }

}
