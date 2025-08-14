package de.uol.pgdoener.th1.domain.tablestructure.service;

import de.uol.pgdoener.th1.application.dto.ReportDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.application.dto.TableStructureGenerationSettingsDto;
import de.uol.pgdoener.th1.domain.analyzeTable.service.AnalyzeMatrixInfoService;
import de.uol.pgdoener.th1.domain.converterchain.model.ConverterChain;
import de.uol.pgdoener.th1.domain.converterchain.service.ConverterChainCreationService;
import de.uol.pgdoener.th1.domain.converterchain.service.ConverterChainService;
import de.uol.pgdoener.th1.domain.fileprocessing.service.FileProcessingService;
import de.uol.pgdoener.th1.domain.shared.model.BuildResult;
import de.uol.pgdoener.th1.domain.shared.model.ConverterResult;
import de.uol.pgdoener.th1.domain.tablestructure.builder.TableStructureBuilder;
import de.uol.pgdoener.th1.domain.tablestructure.exception.TableStructureGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service for generating table structure from a given input file.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTableStructureService {

    private final AnalyzeMatrixInfoService analyzeMatrixInfoService;
    private final ConverterChainCreationService converterChainCreationService;
    private final ConverterChainService converterChainService;
    private final FileProcessingService fileProcessingService;

    /**
     * Generates a table structure from the given input file using the provided settings.
     * <p>
     * This is a convenience method that calls
     * {@link #generateTableStructure(MultipartFile, TableStructureGenerationSettingsDto, Optional)}
     * with {@link Optional#empty()} as the page parameter.
     *
     * @param file     The uploaded file containing the table data (e.g., PDF, image, spreadsheet).
     * @param settings Settings controlling table structure generation (e.g., max iterations, detection options).
     * @return A {@link Pair} where:
     * <ul>
     *   <li>Left: the generated {@link TableStructureDto} representing the table layout</li>
     *   <li>Right: a list of {@link ReportDto} containing unresolved or informational reports from analysis</li>
     * </ul>
     * @throws TableStructureGenerationException if table generation fails unexpectedly.
     */
    public Pair<TableStructureDto, List<ReportDto>> generateTableStructure(
            MultipartFile file, TableStructureGenerationSettingsDto settings
    ) {
        return generateTableStructure(file, settings, Optional.empty());
    }

    /**
     * Generates a table structure from the given input file, optionally targeting a specific page.
     * <p>
     * Steps:
     * <ol>
     *     <li>Processes the uploaded file into a string matrix (rows x columns).</li>
     *     <li>Initializes a {@link TableStructureBuilder} with the provided settings.</li>
     *     <li>If a partial table structure is provided, applies its converters to the matrix.</li>
     *     <li>Iteratively analyzes the matrix, building and refining the table structure until:
     *         <ul>
     *             <li>No new structures are detected, or</li>
     *             <li>The maximum number of iterations is reached.</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param file     The uploaded file containing the table data.
     * @param settings Settings controlling table structure generation.
     * @param page     An optional page number to process (empty = all/default).
     * @return A {@link Pair} where:
     * <ul>
     *   <li>Left: the generated {@link TableStructureDto} representing the table layout</li>
     *   <li>Right: a list of {@link ReportDto} containing unresolved or informational reports from analysis</li>
     * </ul>
     * @throws TableStructureGenerationException if table generation fails unexpectedly.
     */
    public Pair<TableStructureDto, List<ReportDto>> generateTableStructure(
            MultipartFile file, TableStructureGenerationSettingsDto settings, Optional<Integer> page
    ) {
        try {
            log.debug("Start generating table structure for file: {}", file.getOriginalFilename());
            // read file
            String[][] matrix = fileProcessingService.process(file, page);

            // setup defaults
            TableStructureBuilder tableStructureBuilder = new TableStructureBuilder(settings);
            TableStructureDto tableStructure = tableStructureBuilder.getTableStructure();
            BuildResult result = null;

            // run converterChain if overhead converters were enabled
            String[][] convertedMatrix = runIfConvertersPresent(tableStructure, matrix);

            int previousStructureCount = tableStructure.getStructures().size();
            int maxIterations = Math.max(settings.getMaxIterations().orElse(5), 1);
            for (int i = 0; i < maxIterations; i++) {
                List<ReportDto> reports = analyzeMatrixInfoService.analyze(convertedMatrix, settings);
                log.debug("Generated {} reports", reports.size());

                result = tableStructureBuilder.buildTableStructure(reports, settings);
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
        ConverterChain converterChain = converterChainCreationService.create(tableStructure);
        String[][] outputMatrix = converterChainService.performTransformation(inputMatrix, converterChain);
        ConverterResult result = new ConverterResult(tableStructure, outputMatrix);
        return result.data();
    }

}
