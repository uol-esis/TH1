package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureGenerationSettingsDto;
import de.uol.pgdoener.th1.business.infrastructure.BuildResult;
import de.uol.pgdoener.th1.business.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.ConverterChainService;
import de.uol.pgdoener.th1.business.infrastructure.exceptions.TableStructureGenerationException;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.AnalyzeMatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.TableStructureBuilder;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.MatrixInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service for generating table structure from a given input file.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateTableStructureService {

    private final MatrixInfoFactory matrixInfoFactory;
    private final AnalyzeMatrixInfoService analyzeMatrixInfoService;

    /**
     * Main entry point to generate a table structure from the input file.
     *
     * @return the generated table structure DTO.
     */
    public Pair<TableStructureDto, List<ReportDto>> generateTableStructure(
            InputFile inputFile, TableStructureGenerationSettingsDto settings
    ) {
        try {
            log.debug("Start generating table structure for file: {}", inputFile.getFileName());
            // read file
            String[][] matrix = inputFile.asStringArray();

            // setup defaults
            TableStructureBuilder tableStructureBuilder = new TableStructureBuilder(settings);
            TableStructureDto tableStructure = tableStructureBuilder.getTableStructure();
            BuildResult result = null;

            // run converterChain if overhead converters were enabled
            String[][] convertedMatrix = runIfConvertersPresent(tableStructure, matrix);

            int previousStructureCount = tableStructure.getStructures().size();
            int maxIterations = Math.max(settings.getMaxIterations().orElse(5), 1);
            for (int i = 0; i < maxIterations; i++) {
                MatrixInfo matrixInfo = matrixInfoFactory.createParallel(convertedMatrix);

                List<ReportDto> reports = analyzeMatrixInfoService.analyze(matrixInfo, convertedMatrix, settings);
                log.debug("Generated {} reports", reports.size());

                result = tableStructureBuilder.buildTableStructure(reports);
                tableStructure = result.tableStructure();
                log.debug(tableStructure.toString());
                log.debug(reports.toString());

                // continue, if an added structure requires reanalysis of the table
                if (result.requiresReanalysis()) {
                    convertedMatrix = runConverter(matrix, tableStructure);
                    continue;
                }

                // break if no structures have been added since the last iteration
                if (previousStructureCount == tableStructure.getStructures().size()) break;
                previousStructureCount = tableStructure.getStructures().size();

                convertedMatrix = runConverter(matrix, tableStructure);
            }

            log.debug("Successfully generated table structure: {}", tableStructure.getName());
            return Pair.of(result.tableStructure(), result.unresolvedReports());
        } catch (Exception e) {
            log.error("Unexpected error during table structure generation", e);
            throw new TableStructureGenerationException("Unexpected error during table structure generation", e);
        }
    }

    private String[][] runIfConvertersPresent(TableStructureDto tableStructure, String[][] inputMatrix) {
        if (!tableStructure.getStructures().isEmpty()) {
            return runConverter(inputMatrix, tableStructure);
        }
        return inputMatrix;
    }

    private String[][] runConverter(String[][] inputMatrix, TableStructureDto tableStructure) {
        if (tableStructure.getStructures().isEmpty()) return inputMatrix;
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);
        ConverterResult result = converterChainService.performTransformation(inputMatrix);
        return result.data();
    }

}
