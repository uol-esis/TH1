package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import de.uol.pgdoener.th1.business.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;
import de.uol.pgdoener.th1.data.repository.StructureRepository;
import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableStructureService {

    private final TableStructureRepository tableStructureRepository;
    private final StructureRepository structureRepository;
    private final PlatformTransactionManager transactionManager;
    private final ValidationService validationService;
    private final GenerateTableStructureService generateTableStructureService;

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

    @Transactional
    public TableStructureDto getById(Long id) {
        validationService.validateTableStructureExists(id);
        TableStructure tableStructure = tableStructureRepository.getReferenceById(id);

        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructure.getId());
        return TableStructureMapper.toDto(tableStructure, structureList);
    }

    /**
     * Generates a table structure for the given file.
     * It returns the generated table structure and a list of reports which could not be resolved by the generator.
     * The settings might not contain any values but must not be null.
     *
     * @param file             the file to create the table structure for
     * @param optionalSettings setting for the generation
     * @return the generated table structure and unresolved reports
     */
    public Pair<TableStructureDto, List<ReportDto>> generateTableStructure(
            MultipartFile file,
            TableStructureGenerationSettingsDto optionalSettings
    ) {
        InputFile inputFile = new InputFile(file);
        return generateTableStructureService.generateTableStructure(inputFile, optionalSettings);
    }

    @Transactional
    public void deleteById(long id) {
        log.debug("Deleting table structure with id {}", id);
        validationService.validateTableStructureExists(id);

        tableStructureRepository.deleteById(id);
        structureRepository.deleteByTableStructureId(id);
        log.debug("Deleted table structure with id {}", id);
    }

}