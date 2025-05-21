package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatrixInfoFactory {

    private final CellInfoFactory cellInfoFactory;

    public MatrixInfo createParallel(String[][] matrix) {
        CellInfo[][] columnCellInfos = new CellInfo[matrix[0].length][matrix.length];

        final List<RowInfo> rowInfos = IntStream.range(0, matrix.length)
                .parallel()
                .mapToObj(rowIndex -> {
                    List<CellInfo> rowCells = new ArrayList<>(matrix[rowIndex].length);

                    for (int columnIndex = 0; columnIndex < matrix[rowIndex].length; columnIndex++) {
                        CellInfo cellInfo = cellInfoFactory.create(rowIndex, columnIndex, matrix[rowIndex][columnIndex]);
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

    public MatrixInfo create(String[][] matrix) {
        final List<RowInfo> rowInfos = new ArrayList<>(matrix.length);
        CellInfo[][] columnCellInfos = new CellInfo[matrix[0].length][matrix.length];

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            List<CellInfo> rowCells = new ArrayList<>(matrix[rowIndex].length);

            for (int columnIndex = 0; columnIndex < matrix[rowIndex].length; columnIndex++) {
                CellInfo cellInfo = cellInfoFactory.create(rowIndex, columnIndex, matrix[rowIndex][columnIndex]);
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

}
