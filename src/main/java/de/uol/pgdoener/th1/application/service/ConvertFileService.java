package de.uol.pgdoener.th1.application.service;

import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.application.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.domain.converterchain.factory.ConverterChainFactory;
import de.uol.pgdoener.th1.domain.converterchain.model.ConverterChain;
import de.uol.pgdoener.th1.domain.converterchain.service.ConverterChainService;
import de.uol.pgdoener.th1.domain.datatable.service.CreateDatabaseService;
import de.uol.pgdoener.th1.domain.shared.model.ConverterResult;
import de.uol.pgdoener.th1.domain.shared.model.InputFile;
import de.uol.pgdoener.th1.infastructure.metabase.MBService;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import de.uol.pgdoener.th1.infastructure.persistence.entity.TableStructure;
import de.uol.pgdoener.th1.infastructure.persistence.repository.StructureRepository;
import de.uol.pgdoener.th1.infastructure.persistence.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConvertFileService {

    private final MBService mbService;
    private final TableStructureRepository tableStructureRepository;
    private final StructureRepository structureRepository;
    private final ConverterChainFactory converterChainFactory;
    private final CreateDatabaseService createDatabaseService;
    private final ConverterChainService converterChainService;

    public void convertAndSaveInDB(Long tableStructureId, Optional<String> optionalMode, MultipartFile file) {

        String mode = optionalMode.orElse("CREATE");

        Optional<TableStructure> tableStructure = tableStructureRepository.findById(tableStructureId);
        if (tableStructure.isEmpty()) {
            log.debug("Could not find table structure with id {}", tableStructureId);
            throw new IllegalArgumentException("Could not find table structure with id " + tableStructureId);
        }

        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructureId);
        TableStructureDto tableStructureDto = TableStructureMapper.toDto(tableStructure.get(), structureList);
        ConverterChain converterChain = converterChainFactory.create(tableStructureDto);
        InputFile inputFile = new InputFile(file);

        String[][] transformedMatrix = converterChainService.performTransformation(inputFile, converterChain);

        String originalName = file.getOriginalFilename();
        createDatabaseService.create(mode, originalName, transformedMatrix);

        mbService.updateAllDatabases();
    }

    public ConverterResult convertTest(TableStructureDto tableStructureDto, MultipartFile file) {
        InputFile inputFile = new InputFile(file);
        ConverterChain converterChain = converterChainFactory.create(tableStructureDto);
        try {
            String[][] transformedMatrix = converterChainService.performTransformation(inputFile, converterChain);
            return new ConverterResult(tableStructureDto, transformedMatrix);
        } catch (Exception e) {
            throw new RuntimeException("Could not convert file: " + file.getOriginalFilename(), e);
        }
    }

}
