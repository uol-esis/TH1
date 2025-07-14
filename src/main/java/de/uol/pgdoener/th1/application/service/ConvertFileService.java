package de.uol.pgdoener.th1.application.service;

import de.uol.pgdoener.th1.application.converterchain.builder.ConverterChainBuilder;
import de.uol.pgdoener.th1.application.converterchain.model.ConverterChain;
import de.uol.pgdoener.th1.application.converterchain.service.ConverterChainService;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.application.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.application.infrastructure.InputFile;
import de.uol.pgdoener.th1.application.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.application.service.datatable.service.CreateDatabaseService;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;
import de.uol.pgdoener.th1.data.repository.StructureRepository;
import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import de.uol.pgdoener.th1.metabase.MBService;
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
    private final ConverterChainBuilder converterChainBuilder;
    private final CreateDatabaseService createDatabaseService;

    public void convertAndSaveInDB(Long tableStructureId, Optional<String> optionalMode, MultipartFile file) {

        String mode = optionalMode.orElse("CREATE");

        Optional<TableStructure> tableStructure = tableStructureRepository.findById(tableStructureId);
        if (tableStructure.isEmpty()) {
            log.debug("Could not find table structure with id {}", tableStructureId);
            throw new IllegalArgumentException("Could not find table structure with id " + tableStructureId);
        }

        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructureId);
        TableStructureDto tableStructureDto = TableStructureMapper.toDto(tableStructure.get(), structureList);
        ConverterChain converterChain = converterChainBuilder.build(tableStructureDto);
        ConverterChainService converterService = new ConverterChainService(tableStructureDto, converterChain);

        InputFile inputFile = new InputFile(file);
        String[][] transformedMatrix = converterService.performTransformation(inputFile).data();

        String originalName = file.getOriginalFilename();
        createDatabaseService.create(mode, originalName, transformedMatrix);

        mbService.updateAllDatabases();
    }

    public ConverterResult convertTest(TableStructureDto tableStructureDto, MultipartFile file) {
        InputFile inputFile = new InputFile(file);
        ConverterChainService converterService = new ConverterChainService(tableStructureDto);
        try {
            return converterService.performTransformation(inputFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not convert file: " + file.getOriginalFilename(), e);
        }
    }

}
