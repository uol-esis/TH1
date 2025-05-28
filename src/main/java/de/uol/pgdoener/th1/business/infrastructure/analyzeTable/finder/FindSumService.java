package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder;

import de.uol.pgdoener.th1.business.dto.SumReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.RowInfo;
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
                .parallel()
                .filter(rowInfo -> rowInfo.cellInfos().stream()
                        .anyMatch(cellInfo -> {
                            String entry = matrix[cellInfo.rowIndex()][cellInfo.columnIndex()];
                            return isInBlockList(entry, patterns);
                        })
                )
                .map(RowInfo::rowIndex)
                .toList();
    }

    private boolean isInBlockList(String entry, List<Predicate<String>> patterns) {
        return patterns.stream().anyMatch(pattern -> pattern.test(entry));
    }

}
