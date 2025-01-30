package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.TableStructuresApiDelegate;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.business.service.TableStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TableStructureController implements TableStructuresApiDelegate {

    private final TableStructureService tableStructureService;

    @Override
    public ResponseEntity<String> createTableStructure(TableStructureDto request) {
        tableStructureService.create(request);

        return ResponseEntity.ok("TableStructure created");
    }

    @Override
    public ResponseEntity<TableStructureDto> getTableStructure(Long id) {
        TableStructureDto tableStructureDto = tableStructureService.getById(id);

        return ResponseEntity.ok(tableStructureDto);
    }

    @Override
    public ResponseEntity<List<TableStructureSummaryDto>> getTableStructures() {
        List<TableStructureSummaryDto> tableStructuresDto = tableStructureService.getAll();

        return ResponseEntity.ok(tableStructuresDto);
    }

}
