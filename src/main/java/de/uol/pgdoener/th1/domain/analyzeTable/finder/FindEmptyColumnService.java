package de.uol.pgdoener.th1.domain.analyzeTable.finder;

import de.uol.pgdoener.th1.application.dto.EmptyColumnReportDto;
import de.uol.pgdoener.th1.domain.analyzeTable.model.ColumnInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.ColumnInfoService;
import de.uol.pgdoener.th1.domain.analyzeTable.model.MatrixInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindEmptyColumnService {

    private final ColumnInfoService columnInfoService;

    public Optional<EmptyColumnReportDto> find(MatrixInfo matrixInfo) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();

        List<Integer> emptyColumnIndices = columnInfos.stream()
                .filter(columnInfoService::isEmpty)
                .map(ColumnInfo::columnIndex)
                .toList();

        if (emptyColumnIndices.isEmpty()) {
            return Optional.empty();
        }
        EmptyColumnReportDto emptyRowReport = new EmptyColumnReportDto()
                .columnIndex(emptyColumnIndices);
        return Optional.of(emptyRowReport);
    }

}
