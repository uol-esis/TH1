package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.TableStructureDto;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

public record ConverterResult(TableStructureDto tableStructure, String[][] data) {

    public ByteArrayOutputStream dataAsStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            for (String[] row : data) {
                writer.write(String.join(tableStructure.getDelimiter(), row));
                writer.newLine();
            }
        }
        return outputStream;
    }

    // Convert the data to CSV format and return it as a ByteArrayOutputStream
    public String dataAsCsv() {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = dataAsStream();
        } catch (IOException e) {
            throw new RuntimeException("Could not convert data to CSV", e);
        }
        return outputStream.toString();
    }

    public String dataAsJson() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writer.write("[\n");

            // Überprüfen, ob alle Elemente in der ersten Zeile Strings sind
            boolean allStringsInFirstRow = true;
            for (Object value : data[0]) {
                if (!(value instanceof String)) {
                    allStringsInFirstRow = false;
                    break;
                }
            }

            // Wenn alle Werte in der ersten Zeile Strings sind, verwenden wir sie als Schlüssel
            if (allStringsInFirstRow) {
                for (int i = 1; i < data.length; i++) {  // Beginne bei i = 1, um die erste Zeile als Header zu verwenden
                    String[] row = data[i];
                    writer.write("  {\n");
                    for (int j = 0; j < row.length; j++) {
                        writer.write(String.format("    \"%s\": \"%s\"", data[0][j], row[j]));  // Verwende data[0][j] als Schlüssel
                        if (j < row.length - 1) {
                            writer.write(",");
                        }
                        writer.newLine();
                    }
                    writer.write(i < data.length - 1 ? "  },\n" : "  }\n");
                }
            } else {
                // Fallback auf Standard-Schlüssel (column1, column2, ...) falls nicht alle Strings
                for (int i = 0; i < data.length; i++) {
                    String[] row = data[i];
                    writer.write("  {\n");
                    for (int j = 0; j < row.length; j++) {
                        writer.write(String.format("    \"column%d\": \"%s\"", j + 1, row[j]));
                        if (j < row.length - 1) {
                            writer.write(",");
                        }
                        writer.newLine();
                    }
                    writer.write(i < data.length - 1 ? "  },\n" : "  }\n");
                }
            }
            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
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
