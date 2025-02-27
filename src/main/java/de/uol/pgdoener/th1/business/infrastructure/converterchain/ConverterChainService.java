package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterFactory;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.IStructure;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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

    // TODO use an input object instead of MultipartFile
    public ConverterResult performTransformation(MultipartFile file) throws Exception {
        Objects.requireNonNull(converterChain.getFirst());
        String[][] matrix = readCsvToMatrix(file, this.tableStructure);
        String[][] transformedMatrix = converterChain.getFirst().handleRequest(matrix);
        return new ConverterResult(tableStructure, transformedMatrix);
    }

    // TODO move to an input object
    private String[][] readCsvToMatrix(MultipartFile file, TableStructureDto tableStructure) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = CSVFormat.EXCEL.builder()
                    .setDelimiter(tableStructure.getDelimiter())
                    .get()
                    .parse(reader)
                    .stream()
                    .map(record -> record.stream().toArray(String[]::new))
                    .toList();

            int maxRow = rows.size();
            int maxCol = rows.getFirst().length;

            // Falls endRow oder endColumn nicht gesetzt sind, bestimmen wir die Größe dynamisch
            int rowLength = tableStructure.getEndRow().orElse(maxRow);
            int colLength = tableStructure.getEndColumn().orElse(maxCol);
            // Matrix initialisieren
            String[][] matrix = new String[rowLength][colLength];

            // Daten in die Matrix kopieren
            for (int i = 0; i < rowLength && i < rows.size(); i++) {
                System.arraycopy(rows.get(i), 0, matrix[i], 0, Math.min(rows.get(i).length, colLength));
            }

            return matrix;
        }
    }


}
