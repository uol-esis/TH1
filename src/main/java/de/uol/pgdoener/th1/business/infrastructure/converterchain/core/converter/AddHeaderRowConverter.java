package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.HeaderRowStructure;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddHeaderRowConverter extends Converter {

    private final HeaderRowStructure structure;

    @Override
    public String[][] handleRequest(String[][] matrix) throws Exception {
        String[] row = structure.headerRows();

        if (row.length > matrix[0].length) {
            throw new IllegalArgumentException("Header row length exceeds matrix column count");
        }

        System.arraycopy(row, 0, matrix[0], 0, row.length);

        return super.handleRequest(matrix);
    }
}
