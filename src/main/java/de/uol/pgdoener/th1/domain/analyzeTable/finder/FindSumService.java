package de.uol.pgdoener.th1.domain.analyzeTable.finder;

import de.uol.pgdoener.th1.application.dto.SumReportDto;
import de.uol.pgdoener.th1.domain.analyzeTable.model.CellInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.ColumnInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.MatrixInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.RowInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindSumService {

    public Optional<SumReportDto> find(MatrixInfo matrixInfo, String[][] matrix, List<String> blockList) {
        List<Predicate<String>> patterns = preparePatterns(blockList);

        List<Integer> columnsWithSum = getColumnsWithSum(matrixInfo, matrix, patterns);
        List<Integer> rowsWithSum = getRowsSum(matrixInfo, matrix, patterns);

        if (columnsWithSum.isEmpty() && rowsWithSum.isEmpty())
            return Optional.empty();

        SumReportDto sumReport = new SumReportDto()
                .columnIndex(columnsWithSum)
                .rowIndex(rowsWithSum);
        return Optional.of(sumReport);
    }

    private List<Predicate<String>> preparePatterns(List<String> blockList) {
        return blockList.stream()
                .map(value -> Pattern.compile("^" + Pattern.quote(value) + "\\b.*"))
                .map(Pattern::asPredicate)
                .toList();
    }

    private List<Integer> getColumnsWithSum(MatrixInfo matrixInfo, String[][] matrix, List<Predicate<String>> patterns) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        List<Integer> columnsWithSum = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfos) {
            CellInfo firstCell = columnInfo.cellInfos().getFirst();
            String entry = matrix[firstCell.rowIndex()][firstCell.columnIndex()];

            if (isInBlockList(entry, patterns)) columnsWithSum.add(firstCell.columnIndex());
        }
        return columnsWithSum;
    }

    private List<Integer> getRowsSum(MatrixInfo matrixInfo, String[][] matrix, List<Predicate<String>> patterns) {
        return matrixInfo.rowInfos().stream()
                .parallel() // Remove to handle longer burst loads
                .filter(rowInfo -> {
                            for (CellInfo cellInfo : rowInfo.cellInfos()) {
                                String entry = matrix[cellInfo.rowIndex()][cellInfo.columnIndex()];
                                entry = entry.toLowerCase();
                                if (isInBlockList(entry, patterns)) {
                                    return true; // Found a cell that matches the block list
                                }
                            }
                            return false; // No matching cell found in this row
                        }
                )
                .map(RowInfo::rowIndex)
                .toList();
    }

    private boolean isInBlockList(String entry, List<Predicate<String>> patterns) {
        for (Predicate<String> pattern : patterns) {
            if (pattern.test(entry)) {
                return true;
            }
        }
        return false;
    }

}
