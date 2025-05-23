package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.analyze;

import de.uol.pgdoener.th1.business.dto.EmptyHeaderReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.RowInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.RowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindEmptyHeaderService {

    private final RowInfoService rowInfoService;

    public Optional<EmptyHeaderReportDto> find(MatrixInfo matrixInfo) {
        RowInfo firstRow = matrixInfo.rowInfos().getFirst();

        List<Integer> emptyPositions = rowInfoService.getEmptyPositions(firstRow);

        if (emptyPositions.isEmpty()) {
            return Optional.empty();
        }
        EmptyHeaderReportDto emptyRowReport = new EmptyHeaderReportDto()
                .columnIndex(emptyPositions);
        return Optional.of(emptyRowReport);
    }

}
