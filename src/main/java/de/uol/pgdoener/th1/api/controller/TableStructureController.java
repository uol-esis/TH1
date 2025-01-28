package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.payload.request.CreateTableStructure;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructuresDto;
import de.uol.pgdoener.th1.business.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.business.service.TableStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("v1/table-structures")
@RequiredArgsConstructor
public class TableStructureController {

    private final TableStructureService tableStructureService;

    @PostMapping
    public ResponseEntity<String> createTableStructure(@RequestBody @Valid CreateTableStructure request) {
        TableStructureDto tableStructureDto = TableStructureMapper.toDto(request);
        tableStructureService.create(tableStructureDto);

        return ResponseEntity.ok("TableStructure created");
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<TableStructureDto> getTableStructures(@PathVariable("id") Long id) {
        TableStructureDto tableStructureDto = tableStructureService.getById(id);

        return ResponseEntity.ok(tableStructureDto);
    }

    @GetMapping
    @RequestMapping()
    public ResponseEntity<TableStructuresDto> getTableStructures() {
        TableStructuresDto tableStructuresDto = tableStructureService.getAll();

        return ResponseEntity.ok(tableStructuresDto);
    }

}
