package de.uol.pgdoener.th1.business.infrastructure.csv_converter;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.ConverterFactory;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.IStructure;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
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
            int colLength = tableStructure.getEndRow();
            int rowLength = tableStructure.getEndColumn();
            String[][] matrix = new String[colLength][rowLength];

            Iterable<CSVRecord> records = CSVFormat.EXCEL.builder()
                    .setDelimiter(tableStructure.getDelimiter())
                    .get().parse(reader);

            Iterator<CSVRecord> iterator = records.iterator();
            for (int i = 0; iterator.hasNext() && i < colLength; i++) {
                CSVRecord r = iterator.next();
                String[] row = r.stream().toArray(String[]::new);
                System.arraycopy(row, 0, matrix[i], 0, rowLength);
            }
            return matrix;
        }
    }

}
