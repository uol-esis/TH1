package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze;

import de.uol.pgdoener.th1.business.dto.SumReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindSumService {

    public Optional<SumReportDto> find(MatrixInfo matrixInfo, String[][] matrix, List<String> blockList) {

        List<Integer> columnsWithSum = getColumnsWithSum(matrixInfo, matrix, blockList);
        List<Integer> rowsWithSum = getRowsSum(matrixInfo, matrix, blockList);

        if (columnsWithSum.isEmpty() && rowsWithSum.isEmpty())
            return Optional.empty();

        SumReportDto sumReport = new SumReportDto()
                .columnIndex(columnsWithSum)
                .rowIndex(rowsWithSum);
        return Optional.of(sumReport);
    }

    private List<Integer> getColumnsWithSum(MatrixInfo matrixInfo, String[][] matrix, List<String> blockList) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        List<Integer> columnsWithSum = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfos) {
            CellInfo firstCell = columnInfo.cellInfos().getFirst();
            String entry = matrix[firstCell.rowIndex()][firstCell.columnIndex()];

            if (isInBlockList(entry, blockList)) columnsWithSum.add(firstCell.columnIndex());
        }
        return columnsWithSum;
    }

    private List<Integer> getRowsSum(MatrixInfo matrixInfo, String[][] matrix, List<String> blockList) {
        return matrixInfo.rowInfos().stream()
                .filter(rowInfo -> rowInfo.cellInfos().stream()
                        .anyMatch(cellInfo -> {
                            String entry = matrix[cellInfo.rowIndex()][cellInfo.columnIndex()];
                            return isInBlockList(entry, blockList);
                        })
                )
                .map(RowInfo::rowIndex)
                .toList();
    }

    private boolean isInBlockList(String entry, List<String> blockList) {
        for (String value : blockList) {
            if (startsWithValue(entry, value)) return true;
        }
        return false;
    }

    private boolean startsWithValue(String entry, String value) {
        return entry.toLowerCase().matches("^" + Pattern.quote(value) + "\\b.*");
    }

}
