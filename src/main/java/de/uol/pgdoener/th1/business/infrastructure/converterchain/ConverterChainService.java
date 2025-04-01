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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class ConverterChainService {
    private final TableStructureDto tableStructure;
    private final ConverterChain converterChain;

    public ConverterChainService(TableStructureDto tableStructure) {
        this.tableStructure = tableStructure;
        this.converterChain = new ConverterChain();

        for (StructureDto structureDto : this.tableStructure.getStructures()) {
            IStructure structure = StructureMapper.toConverterStructure(structureDto);
            Converter converter = ConverterFactory.createConverter(structure);
            this.converterChain.add(converter);
        }
    }

    public ConverterResult performTransformation(InputFile inputFile) throws TransformationException {
        Objects.requireNonNull(converterChain.getFirst());
        String[][] transformedMatrix;
        try {
            String[][] rawMatrix = inputFile.asStringArray();
            String[][] matrix = cutOffMatrix(rawMatrix, tableStructure);
            transformedMatrix = converterChain.getFirst().handleRequest(matrix);
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
