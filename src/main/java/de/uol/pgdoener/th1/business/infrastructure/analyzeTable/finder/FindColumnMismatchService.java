package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchDto;
import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindColumnMismatchService {
    private final ColumnInfoService columnInfoService;
    private final MatrixInfoService matrixInfoService;

    /**
     * Analyzes the given matrix for column-wise type mismatches.
     * <p>
     * For each column, the expected type is determined (e.g., NUMBER or STRING).
     * Then, all cells below the header are checked:
     * - If a cell's actual type differs from the expected type (excluding NULL),
     * the cell's value is collected as a mismatch candidate.
     * <p>
     * The result is a report containing entries that do not match the expected column type,
     * including the offending value and the indices of the columns in which they appear.
     *
     * @param matrixInfo the structural metadata describing columns and cell types
     * @param matrix     the raw data matrix
     * @return an Optional containing a mismatch report; never empty, but can contain no mismatches
     */
    public Optional<ColumnTypeMismatchReportDto> find(MatrixInfo matrixInfo, String[][] matrix) {
        Map<String, Set<Integer>> mismatchIndex = new HashMap<>();
        int headerEndIndex = matrixInfoService.detectHeaderEndIndex(matrixInfo);

        for (ColumnInfo columnInfo : matrixInfo.columnInfos()) {
            ValueType expectedType = columnInfoService.getType(columnInfo, headerEndIndex);
            int columnIndex = columnInfo.columnIndex();

            for (CellInfo cellInfo : columnInfo.cellInfos()) {
                if (cellInfo.rowIndex() < headerEndIndex) continue;

                ValueType actualType = cellInfo.valueType();
                if (actualType == ValueType.NULL) continue;
                if (isSameValueType(expectedType, actualType)) continue;

                String entry = matrix[cellInfo.rowIndex()][cellInfo.columnIndex()];
                String key = entry.isBlank() ? "" : entry;
                mismatchIndex.computeIfAbsent(key, k -> new HashSet<>()).add(columnIndex);
            }
        }

        ColumnTypeMismatchReportDto report = buildColumnTypeMismatchReportDto(mismatchIndex);
        return Optional.of(report);
    }

    // ----------------- Private helper methods ----------------- //

    /**
     * Determines if the actual value type does not match the expected one.
     */
    private boolean isSameValueType(ValueType expected, ValueType actual) {
        return switch (expected) {
            case NUMBER -> actual == ValueType.NUMBER;
            case STRING -> actual == ValueType.STRING;
            default -> false;
        };
    }

    /**
     * Builds a complete report DTO from the collected mismatched entries.
     */
    private ColumnTypeMismatchReportDto buildColumnTypeMismatchReportDto(Map<String, Set<Integer>> numberIndex) {
        List<ColumnTypeMismatchDto> mismatches = buildMismatches(numberIndex);
        ColumnTypeMismatchReportDto report = new ColumnTypeMismatchReportDto();
        report.setMismatches(mismatches);
        return report;
    }

    /**
     * Constructs a list of mismatch DTOs from the index of problematic entries.
     * Each mismatch includes the affected column indices and suggested replacement.
     */
    private List<ColumnTypeMismatchDto> buildMismatches(Map<String, Set<Integer>> numberIndex) {
        List<ColumnTypeMismatchDto> mismatches = new ArrayList<>();
        numberIndex.forEach((key, indices) -> {
            ColumnTypeMismatchDto mismatch = new ColumnTypeMismatchDto();
            mismatch.setColumnIndex(indices.stream().toList());
            mismatch.setReplacementSearch(Optional.ofNullable(key));

            mismatch.setReplacementValue(isInvalidKey(key) ? Optional.of("*") : Optional.empty());

            mismatches.add(mismatch);
        });
        return mismatches;
    }

    private boolean isInvalidKey(String key) {
        if (key == null || key.isEmpty()) {
            return true;
        }
        return key.length() == 1 && !Character.isLetterOrDigit(key.charAt(0));
    }
}
