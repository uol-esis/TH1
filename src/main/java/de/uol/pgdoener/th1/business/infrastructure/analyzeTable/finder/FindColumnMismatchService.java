package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchDto;
import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindColumnMismatchService {

    private static final String NO_DATA_STRING = "*";

    private final ColumnInfoService columnInfoService;

    public Optional<ColumnTypeMismatchReportDto> find(MatrixInfo matrixInfo, String[][] matrix) {
        List<ColumnTypeMismatchDto> mismatches = matrixInfo.columnInfos().stream()
                .filter(columnInfoService::hasTypeMismatch)
                .map(i -> new ColumnTypeMismatchDto(List.of(i.columnIndex())))
                .toList();
        if (mismatches.isEmpty()) return Optional.empty();

        for (ColumnTypeMismatchDto mismatch : mismatches) {
            ColumnInfo columnInfo = matrixInfo.columnInfos().get(mismatch.getColumnIndex().getFirst());
            Optional<String> noDataString = getNoDataStringIfNumbersColumn(columnInfo, matrix);
            if (noDataString.isPresent() && !noDataString.get().equals(NO_DATA_STRING)) {
                mismatch.replacementSearch(noDataString.get());
                mismatch.replacementValue(NO_DATA_STRING);
            }
        }

        return Optional.of(new ColumnTypeMismatchReportDto().mismatches(mismatches));
    }

    /**
     * This method checks if the given column is column with numbers and strings.
     * If that is the case and the strings are all the same, it returns the string.
     * If the column is not a numbers column or the strings are not all the same,
     * it returns an empty Optional.
     * The returned string is the "no data" string, which can be replaced with the
     * default value for "no data".
     *
     * @param columnInfo the column to check
     * @return the string representing "no data".
     */
    private Optional<String> getNoDataStringIfNumbersColumn(ColumnInfo columnInfo, String[][] matrix) {
        String noDataString = null;

        List<CellInfo> cellInfos = columnInfo.cellInfos();
        // first cell is the header, so we start from index 1
        for (int i = 1; i < cellInfos.size(); i++) {
            CellInfo cellInfo = cellInfos.get(i);
            String entry = matrix[cellInfo.rowIndex()][cellInfo.columnIndex()];

            if (cellInfo.valueType() == ValueType.STRING || cellInfo.valueType() == ValueType.EMPTY) {
                if (noDataString == null) {
                    noDataString = entry;
                } else if (!noDataString.equals(entry)) {
                    return Optional.empty();
                }
            } else if (cellInfo.valueType() != ValueType.NUMBER) {
                return Optional.empty();
            }
        }

        return Optional.ofNullable(noDataString);
    }

}
