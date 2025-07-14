package de.uol.pgdoener.th1.application.analyzeTable.finder;

import de.uol.pgdoener.th1.application.analyzeTable.model.*;
import de.uol.pgdoener.th1.application.dto.TransposeMatrixReportDto;
import de.uol.pgdoener.th1.application.infrastructure.analyzeTable.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindTransposedMatrixService {

    private final CellInfoService cellInfoService;

    /**
     * Detects if the provided matrix is likely a transposed table.
     * A transposed matrix typically has string labels in the first column,
     * numeric values across the first row, and more columns than rows.
     *
     * @param matrixInfo metadata structure (not used directly here but available)
     * @return an Optional containing a TransposedMatrixReportDto if the structure matches
     */
    public Optional<TransposeMatrixReportDto> find(MatrixInfo matrixInfo) {
        double labelRatio = calculateLabelRatioInFirstColumn(matrixInfo);
        double numberRatio = calculateNumberRatioInFirstRow(matrixInfo);

        boolean likelyTransposed = labelRatio > 0.6 && numberRatio > 0.6;

        if (likelyTransposed) {
            TransposeMatrixReportDto report = new TransposeMatrixReportDto();
            report.setDetected(true);
            return Optional.of(report);
        }

        return Optional.empty();
    }

    /**
     * Calculates the proportion of label-like strings in the first column (excluding header).
     */
    private double calculateLabelRatioInFirstColumn(MatrixInfo matrixInfo) {
        ColumnInfo firstColumn = matrixInfo.columnInfos().getFirst();
        int labelCount = 0;
        int total = firstColumn.cellInfos().size();

        for (int i = 0; i < firstColumn.cellInfos().size(); i++) {
            CellInfo cell = firstColumn.cellInfos().get(i);
            if (isLikelyLabel(cell)) {
                labelCount++;
            }
        }

        return total == 0 ? 0 : (double) labelCount / total;
    }

    /**
     * Calculates the proportion of numeric-looking values in the first row (excluding header).
     */
    private double calculateNumberRatioInFirstRow(MatrixInfo matrixInfo) {
        RowInfo firstRow = matrixInfo.rowInfos().getFirst();
        int numberCount = 0;
        int total = firstRow.cellInfos().size();

        for (int i = 0; i < firstRow.cellInfos().size(); i++) {
            CellInfo cell = firstRow.cellInfos().get(i);
            if (isNumber(cell)) {
                numberCount++;
            }
        }

        return total == 0 ? 0 : (double) numberCount / total;
    }

    /**
     * Checks if a string looks like a label (non-empty and not a number).
     */
    private boolean isLikelyLabel(CellInfo cell) {
        return cellInfoService.isString(cell);
    }

    /**
     * Checks if a string can be interpreted as a number.
     * German number format is supported (dot as thousand separator, comma as decimal).
     */
    private boolean isNumber(CellInfo cell) {
        return cell.valueType() == ValueType.NUMBER;
    }
}
