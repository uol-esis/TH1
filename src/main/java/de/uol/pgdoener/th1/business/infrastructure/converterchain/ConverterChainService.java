package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterFactory;
import de.uol.pgdoener.th1.business.infrastructure.exceptions.TransformationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConverterChainService {
    private final TableStructureDto tableStructure;
    private final ConverterChain converterChain;

    public ConverterChainService(@NonNull TableStructureDto tableStructure) {
        this.tableStructure = tableStructure;
        this.converterChain = new ConverterChain();

        List<StructureDto> structures = this.tableStructure.getStructures();
        for (int i = 0; i < structures.size(); i++) {
            Converter converter = ConverterFactory.createConverter(structures.get(i));
            converter.setIndex(i);
            this.converterChain.add(converter);
        }
    }

    public ConverterResult performTransformation(@NonNull InputFile inputFile) throws TransformationException {
        String[][] inputMatrix = inputFile.asStringArray();
        return performTransformation(inputMatrix);
    }

    /**
     * Performs the transformation on the input file.
     * <p>
     * This method reads the input file, applies the transformation defined in the converter chain,
     * and returns the result.
     * If the input file is empty or no converter is found, it returns the original data.
     * If an error occurs during the transformation, it throws a TransformationException.
     * To get more information about the error, check the cause of the exception.
     *
     * @param rawMatrix the input files matrix to be transformed
     * @return the result of the transformation
     * @throws TransformationException if an error occurs during the transformation
     */
    public ConverterResult performTransformation(@NonNull String[][] rawMatrix) throws TransformationException {
        String[][] transformedMatrix;
        String[][] matrix = cutOffMatrix(rawMatrix, tableStructure);
        if (matrix.length == 0 || matrix[0].length == 0) {
            log.debug("No data found in the input file");
            return new ConverterResult(tableStructure, matrix);
        }
        Converter first = converterChain.getFirst();
        if (first == null) {
            log.debug("No converter found");
            return new ConverterResult(tableStructure, rawMatrix);
        }
        transformedMatrix = first.handleRequest(matrix);
        return new ConverterResult(tableStructure, transformedMatrix);
    }

    private static String[][] cutOffMatrix(String[][] inputMatrix, TableStructureDto tableStructure) {
        if (tableStructure.getEndColumn().isPresent() || tableStructure.getEndRow().isPresent()) {

            int maxRow = inputMatrix.length;
            int maxCol = inputMatrix[0].length;

            // Falls endRow oder endColumn nicht gesetzt sind, bestimmen wir die Größe dynamisch
            int rowLength = Math.min(tableStructure.getEndRow().orElse(maxRow), maxRow);
            int colLength = Math.min(tableStructure.getEndColumn().orElse(maxCol), maxCol);

            // Matrix initialisieren
            String[][] outputMatrix = new String[rowLength][colLength];

            // Daten in die Matrix kopieren
            for (int i = 0; i < rowLength; i++) {
                System.arraycopy(inputMatrix[i], 0, outputMatrix[i], 0, Math.min(inputMatrix[i].length, colLength));
            }
            return outputMatrix;
        }
        return inputMatrix;
    }
}
