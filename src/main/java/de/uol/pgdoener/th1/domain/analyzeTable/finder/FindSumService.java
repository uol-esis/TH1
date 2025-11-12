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
        if (blockList == null || blockList.isEmpty()) {
            log.info("Block list is empty, no sums to find.");
            return Optional.empty();
        }
        Predicate<String> pattern = preparePatterns(blockList);

        List<Integer> columnsWithSum = getColumnsWithSum(matrixInfo, matrix, pattern);
        List<Integer> rowsWithSum = getRowsSum(matrixInfo, matrix, pattern);

        if (columnsWithSum.isEmpty() && rowsWithSum.isEmpty())
            return Optional.empty();

        SumReportDto sumReport = new SumReportDto()
                .columnIndex(columnsWithSum)
                .rowIndex(rowsWithSum);
        return Optional.of(sumReport);
    }

    private Predicate<String> preparePatterns(List<String> blockList) {
        StringBuilder sb = new StringBuilder();
        for (String value : blockList) {
            sb.append("(^").append(Pattern.quote(value)).append("\\b.*)|");
        }
        String pattern = sb.toString();
        return Pattern.compile(pattern.substring(0, pattern.length() - 1)).asPredicate();
    }

    private List<Integer> getColumnsWithSum(MatrixInfo matrixInfo, String[][] matrix, Predicate<String> pattern) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        List<Integer> columnsWithSum = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfos) {
            CellInfo firstCell = columnInfo.cellInfos().getFirst();
            String entry = matrix[firstCell.rowIndex()][firstCell.columnIndex()];

            if (isInBlockList(entry, pattern)) columnsWithSum.add(firstCell.columnIndex());
        }
        return columnsWithSum;
    }

    private List<Integer> getRowsSum(MatrixInfo matrixInfo, String[][] matrix, Predicate<String> pattern) {
        return matrixInfo.rowInfos().stream()
                .parallel() // Remove to handle longer burst loads
                .filter(rowInfo -> {
                            for (CellInfo cellInfo : rowInfo.cellInfos()) {
                                String entry = matrix[cellInfo.rowIndex()][cellInfo.columnIndex()];
                                entry = entry.toLowerCase();
                                if (isInBlockList(entry, pattern)) {
                                    return true; // Found a cell that matches the block list
                                }
                            }
                            return false; // No matching cell found in this row
                        }
                )
                .map(RowInfo::rowIndex)
                .toList();
    }

    private boolean isInBlockList(String entry, Predicate<String> pattern) {
        return pattern.test(entry);
    }

}
