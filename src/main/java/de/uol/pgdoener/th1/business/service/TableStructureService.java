package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.GenerateTableStructureService;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import de.uol.pgdoener.th1.business.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;
import de.uol.pgdoener.th1.data.repository.StructureRepository;
import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableStructureService {

    private final TableStructureRepository tableStructureRepository;
    private final StructureRepository structureRepository;
    private final PlatformTransactionManager transactionManager;

    public long create(TableStructureDto tableStructureDto) {
        List<StructureDto> structureDtoList = tableStructureDto.getStructures();
        TableStructure tableStructure = TableStructureMapper.toEntity(tableStructureDto);

        Long result = new TransactionTemplate(transactionManager).execute(
                status -> {
                    try {
                        TableStructure savedTableStructure = tableStructureRepository.save(tableStructure);
                        for (int i = 0; i < structureDtoList.size(); i++) {
                            StructureDto structureDto = structureDtoList.get(i);
                            Structure structure = StructureMapper.toEntity(structureDto, i, savedTableStructure.getId());
                            structureRepository.save(structure);
                        }
                        return tableStructure.getId();
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.info("Error while saving table structure", e);
                        throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                    }
                }
        );
        if (result == null) {
            log.error("Could not get id of created table structure");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /// TODO: Möglicherweise weniger zurück geben. Jetzt werden alle Informationen zurück gegeben.
    public List<TableStructureSummaryDto> getAll() {
        Iterable<TableStructure> tableStructures = tableStructureRepository.findAll();

        List<TableStructureSummaryDto> tableStructuresDto = new ArrayList<>();
        tableStructures.forEach(tableStructure -> {
            List<Structure> structureList = structureRepository.findByTableStructureId(tableStructure.getId());
            TableStructureSummaryDto tableStructureSummaryDto = TableStructureMapper.toSummaryDto(tableStructure, structureList);
            tableStructuresDto.add(tableStructureSummaryDto);
        });

        return tableStructuresDto;
    }

    public TableStructureDto getById(Long id) {
        TableStructure tableStructure = tableStructureRepository.findById(id).orElse(null);
        if (tableStructure == null) {
            log.info("Table structure with id {} not found", id);
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "kein Eintrag");
        }

        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructure.getId());
        return TableStructureMapper.toDto(tableStructure, structureList);
    }

    public TableStructureDto generateTableStructure(MultipartFile file) {
        InputFile inputFile = new InputFile(file);
        GenerateTableStructureService generateTableStructureService = new GenerateTableStructureService(inputFile);
        try {
            return generateTableStructureService.generateTableStructure();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteById(long id) {
        Optional<TableStructure> tableStructure = tableStructureRepository.findById(id);
        if (tableStructure.isEmpty()) {
            log.info("Table structure with id {} not found", id);
            return false;
        }
        Boolean success = new TransactionTemplate(transactionManager).execute(
                status -> {
                    try {
                        tableStructureRepository.deleteById(id);
                        structureRepository.deleteByTableStructureId(id);
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.info("Error while deleting table structure", e);
                        throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                    }
                    return true;
                }
        );
        return success != null && success;
    }

}