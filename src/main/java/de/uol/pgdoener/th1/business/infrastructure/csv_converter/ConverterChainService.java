package de.uol.pgdoener.th1.business.infrastructure.csv_converter;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.ConverterFactory;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.IStructure;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

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

    public ByteArrayOutputStream performTransformation(InputFile file) throws Exception {
        String[][] matrix = file.asStringArray();
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
                writer.write(String.join(String.valueOf(tableStructure.getDelimiter()), row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
