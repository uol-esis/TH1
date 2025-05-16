package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureGenerationSettingsDto;
import de.uol.pgdoener.th1.business.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.ConverterChainService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.AnalyzeMatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.MatrixInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.MatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.TableStructureBuilder;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Remove empty cells at the end in the row
// mehrer Einträge in einer Zeile -> Converter Schreiben der eine column index braucht und dann nach abstätzrn /n die eintrage entschachtelt.
// Wie gehen wir mit leeren Werten um ? // wichtig für die Datenbank kann mit * und anderen Symbolen nicht umgehen :(
// Bulk import fix Postgres
// Code schön machen

/**
 * Service for generating table structure from a given input file.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateTableStructureService {

    private final MatrixInfoService matrixInfoService;
    private final MatrixInfoFactory matrixInfoFactory;
    private final AnalyzeMatrixInfoService analyzeMatrixInfoService;

    /**
     * Main entry point to generate a table structure from the input file.
     *
     * @return the generated table structure DTO.
     * @throws IOException if the file cannot be read.
     */
    public Pair<TableStructureDto, List<ReportDto>> generateTableStructure(
            InputFile inputFile, TableStructureGenerationSettingsDto settings
    ) throws IOException {
        try {
            log.debug("Start generating table structure for file: {}", inputFile.getFileName());
            String[][] matrix = inputFile.asStringArray();

            TableStructureBuilder tableStructureBuilder = new TableStructureBuilder(settings);
            TableStructureDto tableStructure = tableStructureBuilder.getTableStructure();
            Pair<TableStructureDto, List<ReportDto>> result = null;
            for (int i = 0; i < 5; i++) {
                String[][] convertedMatrix = runConverter(matrix, tableStructure);
                MatrixInfo matrixInfo = matrixInfoFactory.create(convertedMatrix);
                List<ReportDto> reports = analyzeMatrixInfoService.analyze(matrixInfo);
                log.debug("Generated {} reports", reports.size());
                result = tableStructureBuilder.buildTableStructure(reports);
                tableStructure = result.getFirst();
            }

            log.debug("Successfully generated table structure: {}", tableStructure.getName());
            return result;
        } catch (IOException e) {
            log.warn("Failed to read input file: {}", inputFile.getFileName(), e);
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected error during table structure generation", e);
            throw new RuntimeException("Tabellenstruktur konnte nicht erstellt werden", e);
        }
    }

    private String[][] runConverter(String[][] inputMatrix, TableStructureDto tableStructure) {
        if (tableStructure.getStructures().isEmpty()) return inputMatrix;
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);
        ConverterResult result = converterChainService.performTransformation(inputMatrix);
        return result.data();
    }

    /**
     * Determines if a given row is a data row (based on numeric cell values).
     */
    private boolean isDataRow(String[] row) {
        for (String cell : row) {
            if (isNumeric(cell)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a string row into a RowInfo object with CellInfos.
     */
    private RowInfo extractRowInfo(String[] row, int rowIndex) {
        RowInfo rowInfo = new RowInfo(rowIndex);

        for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
            String cell = row[cellIndex].trim();
            boolean isNotEmpty = !cell.isEmpty();
            rowInfo.addColumnInfo(new CellInfo(cellIndex, isNotEmpty));
        }

        rowInfo.setHeaderName(row[0].trim());
        return rowInfo;
    }


    /**
     * Determines if a string is numeric (supports decimals with dot or comma).
     */
    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        str = str.trim().replace(",", "."); // Falls Komma als Dezimaltrenner genutzt wird
        return str.matches("-?\\d+(\\.\\d+)?"); // Regex für Ganzzahlen & Dezimalzahlen
    }


    /**
     * Counts the number of valid elements in a row.
     * Valid = not null, not empty, not equal to "*"
     */
    private long countValidElements(String[] row) {
        return Arrays.stream(row)
                .filter(entry -> !isInvalidEntry(entry))
                .count();
    }

    /**
     * Returns true if the entry is considered invalid.
     * Invalid = null, empty string, or a literal "*"
     */
    private boolean isInvalidEntry(String entry) {
        return entry == null || entry.trim().isEmpty() || entry.equals("*");
    }
}
