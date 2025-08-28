package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.application.dto.AddHeaderNameStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddHeaderRowConverter extends Converter {

    private final AddHeaderNameStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        String[] newHeader = structure.getHeaderNames().toArray(new String[0]);
        if (newHeader.length > matrix[0].length)
            throwConverterException("Header newHeader length exceeds matrix column count");
        if (newHeader.length == 0) return super.handleRequest(matrix);

        return switch (structure.getHeaderPlacementType()) {
            case REPLACE_FIRST_ROW -> replaceFirstRow(matrix, newHeader);
            case INSERT_AT_TOP -> insertAtTop(matrix, newHeader);
            case UNKNOWN_DEFAULT_OPEN_API -> super.handleRequest(matrix);
        };

    }

    /**
     * Replaces the first row of the matrix with the new header.
     *
     * @param matrix    Original table matrix
     * @param newHeader Array of header names to replace the first row
     * @return The updated matrix after replacing the first row
     */
    private String[][] replaceFirstRow(String[][] matrix, String[] newHeader) {
        System.arraycopy(newHeader, 0, matrix[0], 0, newHeader.length);
        return super.handleRequest(matrix);
    }

    /**
     * Inserts a new row at the top of the matrix, shifting existing rows down by one.
     *
     * @param matrix    Original table matrix
     * @param newHeader Array of header names to insert as the new first row
     * @return The updated matrix after inserting the new header row at the top
     */
    private String[][] insertAtTop(String[][] matrix, String[] newHeader) {
        String[][] newMatrix = new String[matrix.length + 1][];
        String[] normalizedHeader = normalizeHeader(newHeader, matrix[0].length);
        newMatrix[0] = normalizedHeader;
        System.arraycopy(matrix, 0, newMatrix, 1, matrix.length);

        return super.handleRequest(newMatrix);
    }

    private String[] normalizeHeader(String[] header, int length) {
        if (header.length == length) return header;

        String[] newHeader = new String[length];
        for (int i = 0; i < length; i++)
            if (i < header.length) newHeader[i] = header[i];
            else newHeader[i] = "";

        return newHeader;
    }
}
