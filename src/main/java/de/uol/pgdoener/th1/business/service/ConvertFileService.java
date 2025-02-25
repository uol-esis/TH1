package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.ConverterChainService;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.ConverterResult;
import de.uol.pgdoener.th1.business.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;
import de.uol.pgdoener.th1.data.repository.DynamicTableRepository;
import de.uol.pgdoener.th1.data.repository.StructureRepository;
import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConvertFileService {

    //private final MinioClient minioClient;
    private final TableStructureRepository tableStructureRepository;
    private final StructureRepository structureRepository;
    private final DynamicTableRepository dynamicTableRepository;

    public void convertAndSaveInDB(Long tableStructureId, MultipartFile file) {

        Optional<TableStructure> tableStructure = tableStructureRepository.findById(tableStructureId);
        if (tableStructure.isEmpty()) {
            throw new RuntimeException("Could not find table structure with id " + tableStructureId);
        }

        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructureId);

        TableStructureDto tableStructureDto = TableStructureMapper.toDto(tableStructure.get(), structureList);

        ConverterChainService converterService = new ConverterChainService(tableStructureDto);

        try {
            String[][] transformedMatrix = converterService.performTransformation(file).data();

            String tableName = "dynamic_table_" + tableStructureId;

            // Tabelle erstellen
            dynamicTableRepository.createTableIfNotExists(tableName, transformedMatrix);
            // Daten einfügen
            dynamicTableRepository.insertData(tableName, transformedMatrix);
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    public ConverterResult convertTest(TableStructureDto tableStructureDto, MultipartFile file) {
        ConverterChainService converterService = new ConverterChainService(tableStructureDto);
        try {
            return converterService.performTransformation(file);
        } catch (Exception e) {
            throw new RuntimeException("Could not convert file: " + file.getOriginalFilename(), e);
        }
    }

    /*public void convertAndSaveToMinio(ConvertFileDto convertFileDto) {

        Optional<TableStructure> tableStructure = tableStructureRepository.findById(convertFileDto.tableStructureId());
        if (tableStructure.isEmpty()) {
            throw new RuntimeException("Could not find table structure with id " + convertFileDto.tableStructureId());
        }

        long tableStructureId = tableStructure.get().getId();
        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructureId);

        TableStructureDto tableStructureDto = TableStructureMapper.toDto(tableStructure.get(), structureList);

        ConverterChainService converterService = CreateConverterService.createHandler(tableStructureDto);
        try {
            ByteArrayOutputStream transformedFileStream = converterService.performTransformation(convertFileDto.file());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("warehouse")
                            .object("transformed-file.csv")
                            .stream(
                                    new ByteArrayInputStream(transformedFileStream.toByteArray()),
                                    transformedFileStream.size(),
                                    -1 // Part size, -1 für Streaming
                            )
                            .contentType("text/csv")
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Could not convert file " + convertFileDto.file(), e);
        }
    }*/
}
