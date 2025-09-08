package de.uol.pgdoener.th1.application.service;

import de.uol.pgdoener.th1.application.dto.ReportDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.application.dto.TableStructureGenerationSettingsDto;
import de.uol.pgdoener.th1.application.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.application.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.domain.tablestructure.service.GenerateTableStructureService;
import de.uol.pgdoener.th1.domain.tablestructure.service.TableStructureValidationService;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import de.uol.pgdoener.th1.infastructure.persistence.entity.TableStructure;
import de.uol.pgdoener.th1.infastructure.persistence.repository.StructureRepository;
import de.uol.pgdoener.th1.infastructure.persistence.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableStructureService {

    private final TableStructureRepository tableStructureRepository;
    private final StructureService structureService;
    private final StructureRepository structureRepository;
    private final PlatformTransactionManager transactionManager;
    private final TableStructureValidationService tableStructureValidationService;
    private final GenerateTableStructureService generateTableStructureService;

    @Transactional
    public long create(TableStructureDto tableStructureDto) {
        TableStructure tableStructure = TableStructureMapper.toEntity(tableStructureDto);
        tableStructureValidationService.validateName(tableStructure.getName());
        try {
            TableStructure savedTableStructure = tableStructureRepository.save(tableStructure);
            Long tableStructureId = savedTableStructure.getId();
            structureService.createStructures(tableStructureDto.getStructures(), tableStructureId);
            return tableStructureId;
        } catch (Exception e) {
            log.error("Error while saving table structure", e);
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public void updateById(TableStructureDto tableStructureDto, long id) {
        log.debug("Updating table structure with id {}", id);
        tableStructureValidationService.validateTableStructureExists(id);
        TableStructure tableStructure = tableStructureRepository.getReferenceById(id);
        if (!tableStructure.getName().equals(tableStructureDto.getName())) {
            tableStructureValidationService.validateName(tableStructureDto.getName());
        }

        TableStructure updatedTableStructure = TableStructureMapper.toEntity(tableStructureDto);
        structureRepository.deleteByTableStructureId(id);

        tableStructure.setName(updatedTableStructure.getName());
        tableStructure.setEndColumn(updatedTableStructure.getEndColumn());
        tableStructure.setEndRow(updatedTableStructure.getEndRow());
        structureService.createStructures(tableStructureDto.getStructures(), tableStructure.getId());

        tableStructureRepository.save(tableStructure);
        log.debug("Updating table structure with id {}", id);
    }

    /// TODO: Möglicherweise weniger zurück geben. Jetzt werden alle Informationen zurück gegeben.
    public List<TableStructureSummaryDto> getAll() {
        Iterable<TableStructure> tableStructures = tableStructureRepository.findAll();

        List<TableStructureSummaryDto> tableStructuresDto = new ArrayList<>();
        Map<Long, List<Structure>> structureMap = StreamSupport.stream(structureRepository.findAll().spliterator(), false)
                .collect(Collectors.groupingBy(Structure::getTableStructureId));
        tableStructures.forEach(tableStructure -> {
            List<Structure> structureList = structureMap.getOrDefault(tableStructure.getId(), List.of());
            TableStructureSummaryDto tableStructureSummaryDto = TableStructureMapper.toSummaryDto(tableStructure, structureList);
            tableStructuresDto.add(tableStructureSummaryDto);
        });

        return tableStructuresDto;
    }

    @Transactional
    public TableStructureDto getById(Long id) {
        tableStructureValidationService.validateTableStructureExists(id);
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
     * @param page             the excel sheet page
     * @return the generated table structure and unresolved reports
     */
    public Pair<TableStructureDto, List<ReportDto>> generateTableStructure(
            MultipartFile file,
            TableStructureGenerationSettingsDto optionalSettings,
            Optional<Integer> page
    ) {
        return generateTableStructureService.generateTableStructure(file, optionalSettings, page);
    }

    @Transactional
    public void deleteById(long id) {
        log.debug("Deleting table structure with id {}", id);
        tableStructureValidationService.validateTableStructureExists(id);

        tableStructureRepository.deleteById(id);
        structureRepository.deleteByTableStructureId(id);
        log.debug("Deleted table structure with id {}", id);
    }

}