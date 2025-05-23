package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze;

import de.uol.pgdoener.th1.business.dto.PositionDto;
import de.uol.pgdoener.th1.business.dto.SameAsHeaderReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindSameAsHeaderService {

    public Optional<SameAsHeaderReportDto> find(MatrixInfo matrixInfo, String[][] matrix) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();

        List<PositionDto> positionsSameAsHeader = columnInfos.stream()
                .map(columnInfo -> findSameAsHeader(columnInfo, matrix))
                .flatMap(List::stream)
                .toList();

        if (positionsSameAsHeader.isEmpty()) {
            return Optional.empty();
        }
        SameAsHeaderReportDto emptyRowReport = new SameAsHeaderReportDto()
                .cells(positionsSameAsHeader);
        return Optional.of(emptyRowReport);
    }

    // move to columnInfoService although it needs the matrix?
    private List<PositionDto> findSameAsHeader(ColumnInfo columnInfo, String[][] matrix) {
        String header = matrix[0][columnInfo.columnIndex()];
        int maxRowToLookAt = Math.min(columnInfo.cellInfos().size(), 10);

        List<PositionDto> positionsSameAsHeader = new ArrayList<>();
        List<CellInfo> cellInfos = columnInfo.cellInfos();
        for (int rowIndex = 1; rowIndex < maxRowToLookAt; rowIndex++) {
            String cellEntry = matrix[rowIndex][columnInfo.columnIndex()];
            if (cellEntry.equals(header)) {
                CellInfo cellInfo = cellInfos.get(rowIndex);
                positionsSameAsHeader.add(new PositionDto(cellInfo.rowIndex(), cellInfo.columnIndex()));
            }
        }

        return positionsSameAsHeader;
    }

}
