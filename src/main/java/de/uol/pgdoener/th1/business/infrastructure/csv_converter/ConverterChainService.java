package de.uol.pgdoener.th1.business.infrastructure.csv_converter;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.ConverterFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConverterChainService {
    private final TableStructureDto tableStructure;
    private final ConverterChain converterChain;

    public ConverterChainService(TableStructureDto tableStructure) {
        this.tableStructure = tableStructure;
        this.converterChain = new ConverterChain();

        for (StructureDto structure : this.tableStructure.structure()) {
            Converter converter = ConverterFactory.createValidator(structure);
            this.converterChain.add(converter);
        }
    }

    public ByteArrayOutputStream performTransformation(MultipartFile file) throws Exception {
        String[][] matrix = readCsvToMatrix(file, this.tableStructure);
        if (converterChain.getFirst() == null) throw new Exception("First chain is null");
        String[][] transformedMatrix = converterChain.getFirst().handleRequest(matrix);
        return writeMatrixToStream(transformedMatrix);
    }

    /// TODO: Mit oberer Funktion zusammenfassen oder anderes lösen!!
    public String[][] performTransformationGetArray(MultipartFile file) throws Exception {
        String[][] matrix = readCsvToMatrix(file, this.tableStructure);
        if (converterChain.getFirst() == null) throw new Exception("First chain is null");
        return converterChain.getFirst().handleRequest(matrix);
    }

    private String[][] readCsvToMatrix(MultipartFile file, TableStructureDto tableStructure) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line.split(String.valueOf(tableStructure.delimiter()), -1));
            }
            int endRow = tableStructure.endRow();
            int endCol = tableStructure.endColumn();
            String[][] matrix = new String[endRow][endCol];
            for (int i = 0; i < endRow; i++) {
                String[] row = rows.get(i);
                System.arraycopy(row, 0, matrix[i], 0, endCol);
            }
            return matrix;
        }
    }

    private ByteArrayOutputStream writeMatrixToStream(String[][] matrix) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            for (String[] row : matrix) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
        return outputStream;
    }

    private void writeMatrixToFile(String[][] matrix, String fileName) {
        File newFile = new File(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(newFile.toPath())) {
            for (String[] row : matrix) {
                writer.write(String.join(String.valueOf(tableStructure.delimiter()), row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
