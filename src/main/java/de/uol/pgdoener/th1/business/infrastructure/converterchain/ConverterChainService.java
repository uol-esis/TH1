package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterFactory;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.IStructure;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ConverterChainService {
    private final TableStructureDto tableStructure;
    private final ConverterChain converterChain;

    public ConverterChainService(@NonNull TableStructureDto tableStructure) {
        this.tableStructure = tableStructure;
        this.converterChain = new ConverterChain();

        for (StructureDto structureDto : this.tableStructure.getStructures()) {
            IStructure structure = StructureMapper.toConverterStructure(structureDto);
            Converter converter = ConverterFactory.createConverter(structure);
            this.converterChain.add(converter);
        }
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
     * @param inputFile the input file to be transformed
     * @return the result of the transformation
     * @throws TransformationException if an error occurs during the transformation
     */
    public ConverterResult performTransformation(@NonNull InputFile inputFile) throws TransformationException {
        String[][] transformedMatrix;
        try {
            String[][] rawMatrix = inputFile.asStringArray();
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
        } catch (IOException e) {
            log.error("Error processing file: Could not read input file content", e);
            throw new TransformationException("Error processing file: Could not read input file content", e);
        }
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
