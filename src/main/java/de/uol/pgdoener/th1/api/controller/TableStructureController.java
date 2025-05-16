package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.TableStructuresApiDelegate;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureGenerationResponseDto;
import de.uol.pgdoener.th1.business.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.business.service.TableStructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableStructureController implements TableStructuresApiDelegate {

    private final TableStructureService tableStructureService;

    @Override
    public ResponseEntity<Long> createTableStructure(TableStructureDto request) {
        log.debug("Creating table structure {}", request);
        long id = tableStructureService.create(request);
        log.debug("Table structure created");
        return ResponseEntity.status(201).body(id);
    }

    @Override
    public ResponseEntity<TableStructureDto> getTableStructure(Long id) {
        log.debug("Getting table structure with id {}", id);
        TableStructureDto tableStructureDto = tableStructureService.getById(id);
        log.debug("Table structure found");
        return ResponseEntity.ok(tableStructureDto);
    }

    @Override
    public ResponseEntity<List<TableStructureSummaryDto>> getTableStructures() {
        log.debug("Getting all table structures");
        List<TableStructureSummaryDto> tableStructuresDto = tableStructureService.getAll();
        log.debug("Table structures found");
        return ResponseEntity.ok(tableStructuresDto);
    }

    @Override
    public ResponseEntity<Void> deleteTableStructure(Long id) {
        log.debug("Deleting table structure with id {}", id);
        tableStructureService.deleteById(id);
        log.debug("Table structure deleted");
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TableStructureGenerationResponseDto> generateTableStructure(MultipartFile file) {
        TableStructureDto newTableStructureDto = tableStructureService.generateTableStructure(file);
        log.debug("Table structure generated");
        TableStructureGenerationResponseDto responseDto = new TableStructureGenerationResponseDto();
        responseDto.setTableStructure(newTableStructureDto);
        return ResponseEntity.ok(responseDto);
    }

}
