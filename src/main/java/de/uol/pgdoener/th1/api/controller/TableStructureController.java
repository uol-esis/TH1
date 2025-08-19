package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.TableStructuresApiDelegate;
import de.uol.pgdoener.th1.application.dto.*;
import de.uol.pgdoener.th1.application.service.TableStructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableStructureController implements TableStructuresApiDelegate {

    private final TableStructureService tableStructureService;

    @Override
    @PreAuthorize("hasAuthority('write:tablestructure')")
    public ResponseEntity<Long> createTableStructure(TableStructureDto request) {
        log.debug("Creating table structure {}", request);
        long id = tableStructureService.create(request);
        log.debug("Table structure created");
        return ResponseEntity.status(201).body(id);
    }

    @Override
    @PreAuthorize("hasAuthority('read:tablestructure')")
    public ResponseEntity<TableStructureDto> getTableStructure(Long id) {
        log.debug("Getting table structure with id {}", id);
        TableStructureDto tableStructureDto = tableStructureService.getById(id);
        log.debug("Table structure found");
        return ResponseEntity.ok(tableStructureDto);
    }

    @Override
    @PreAuthorize("hasAuthority('read:tablestructure')")
    public ResponseEntity<List<TableStructureSummaryDto>> getTableStructures() {
        log.debug("Getting all table structures");
        List<TableStructureSummaryDto> tableStructuresDto = tableStructureService.getAll();
        log.debug("Table structures found");
        return ResponseEntity.ok(tableStructuresDto);
    }

    @Override
    @PreAuthorize("hasAuthority('write:tablestructure')")
    public ResponseEntity<Void> deleteTableStructure(Long id) {
        log.debug("Deleting table structure with id {}", id);
        tableStructureService.deleteById(id);
        log.debug("Table structure deleted");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('read:tablestructure')")
    public ResponseEntity<TableStructureGenerationResponseDto> generateTableStructure(
            MultipartFile file,
            TableStructureGenerationSettingsDto settings,
            Optional<Integer> page
    ) {
        log.debug("Generating Table structure for file {} with settings {}", file.getOriginalFilename(), settings);
        Pair<TableStructureDto, List<ReportDto>> result = tableStructureService.generateTableStructure(file, settings, page);
        log.debug("Table structure generated");
        TableStructureGenerationResponseDto responseDto = new TableStructureGenerationResponseDto();
        responseDto.setTableStructure(result.getFirst());
        responseDto.setReports(result.getSecond());
        return ResponseEntity.ok(responseDto);
    }

}
