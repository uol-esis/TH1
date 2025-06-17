package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatrixInfoService {

    private final CellInfoService cellInfoService;

    public int detectRectangleWidth(MatrixInfo matrixInfo) {
        int width = 1;
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        for (int i = 1; i < columnInfos.size(); i++) {
            ColumnInfo columnInfo = columnInfos.get(i);
            CellInfo firstCellOfColumn = columnInfo.cellInfos().getFirst();
            if (!cellInfoService.hasEntry(firstCellOfColumn)) {
                width++;
            } else {
                break;
            }
        }
        return width;
    }

    public int detectRectangleHeight(MatrixInfo matrixInfo, int width) {
        int height = 1;
        List<RowInfo> rowInfos = matrixInfo.rowInfos();
        for (int i = 1; i < rowInfos.size(); i++) {
            RowInfo rowInfo = rowInfos.get(i);
            CellInfo firstCellInRow = rowInfo.cellInfos().getFirst();
            CellInfo firstHeaderCell = rowInfo.cellInfos().get(width);
            if (cellInfoService.hasEntry(firstCellInRow) && cellInfoService.hasEntry(firstHeaderCell)) {
                height++;
            } else {
                break;
            }
        }
        return height;
    }

    public int detectHeaderEndIndex(MatrixInfo matrixInfo) {
        int maxChecksPerColumn = 5;

        for (ColumnInfo columnInfo : matrixInfo.columnInfos()) {
            int checks = 0;

            for (CellInfo cellInfo : columnInfo.cellInfos()) {
                if (checks >= maxChecksPerColumn) break;

                if (cellInfo.valueType() == ValueType.NULL || cellInfo.valueType() == ValueType.EMPTY) continue;

                checks++;

                if (cellInfo.valueType() == ValueType.NUMBER) {
                    return cellInfo.rowIndex();
                }
            }
        }
        return 1;
    }

    public boolean isRectangleValid(MatrixInfo matrixInfo, int width, int height) {
        for (int i = 0; i < height; i++) {
            if (cellInfoService.isEmpty(matrixInfo.rowInfos().get(i).cellInfos().getFirst())) {
                return false;
            }
            for (int j = 1; j < width; j++) {
                if (cellInfoService.hasEntry(matrixInfo.rowInfos().get(i).cellInfos().get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidGroupedHeaderColumnHeader(MatrixInfo matrixInfo, int width, int height) {
        RowInfo columnHeaderRow = matrixInfo.rowInfos().get(height);
        for (int i = 0; i < width; i++)
            if (cellInfoService.isEmpty(columnHeaderRow.cellInfos().get(i)))
                return false;

        for (int i = width; i < columnHeaderRow.cellInfos().size(); i++)
            if (cellInfoService.hasEntry(columnHeaderRow.cellInfos().get(i)))
                return false;
        return true;
    }

    public boolean hasFirstEntry(MatrixInfo matrixInfo) {
        CellInfo topLeft = matrixInfo.rowInfos().getFirst().cellInfos().getFirst();
        return cellInfoService.hasEntry(topLeft);
    }

  /*  public int detectColumnHeaderEndIndex(MatrixInfo matrixInfo) {
        List<ColumnInfo> columns = matrixInfo.columnInfos();

        for (int i = 1; i < columns.size(); i++) {
            CellInfo firstCell = columns.get(i).cellInfos().getFirst();

            if (cellInfoService.hasEntry(firstCell)) {
                return i - 1;
            }
        }

        return columns.size();
    }

    public int detectRowHeaderEndIndex(MatrixInfo matrixInfo, int width) {
        List<RowInfo> rows = matrixInfo.rowInfos();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            CellInfo cell = rows.get(rowIndex).cellInfos().get(width + 1);

            if (!cellInfoService.hasEntry(cell)) {
                return rowIndex;
            }

            if (!cellInfoService.isString(cell)) {
                return rowIndex - 1;
            }
        }

        return rows.size();
    }*/

}
