package de.uol.pgdoener.th1.domain.analyzeTable.finder;

import de.uol.pgdoener.th1.application.dto.EmptyRowReportDto;
import de.uol.pgdoener.th1.domain.analyzeTable.model.MatrixInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.RowInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.RowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindEmptyRowService {

    private final RowInfoService rowInfoService;

    public Optional<EmptyRowReportDto> find(MatrixInfo matrixInfo) {
        List<RowInfo> rowInfos = matrixInfo.rowInfos();

        List<Integer> emptyRowIndices = rowInfos.stream()
                .filter(rowInfoService::isEmpty)
                .map(RowInfo::rowIndex)
                .toList();

        if (emptyRowIndices.isEmpty()) {
            return Optional.empty();
        }
        EmptyRowReportDto emptyRowReport = new EmptyRowReportDto()
                .rowIndex(emptyRowIndices);
        return Optional.of(emptyRowReport);
    }

}
