package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.TableStructureDto;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record ConverterResult(TableStructureDto tableStructure, String[][] data) {

    public List<List<String>> dataAsListOfLists() {
        return Stream.of(data).map(List::of).toList();
    }

    public ByteArrayOutputStream dataAsCsvStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            for (String[] row : data) {
                writer.write(String.join(tableStructure.getDelimiter(), row));
                writer.newLine();
            }
        }
        return outputStream;
    }

    public void writeMatrixToFile(String[][] matrix, String fileName) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConverterResult that = (ConverterResult) o;
        return Objects.deepEquals(data, that.data) && Objects.equals(tableStructure, that.tableStructure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableStructure, Arrays.deepHashCode(data));
    }

}
