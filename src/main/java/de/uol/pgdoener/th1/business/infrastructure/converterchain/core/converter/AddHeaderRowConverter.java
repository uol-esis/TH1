package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.AddHeaderNameStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddHeaderRowConverter extends Converter {

    private final AddHeaderNameStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        String[] row = structure.getHeaderNames().toArray(new String[0]);

        if (row.length > matrix[0].length) {
            throwConverterException("Header row length exceeds matrix column count");
        }

        System.arraycopy(row, 0, matrix[0], 0, row.length);

        return super.handleRequest(matrix);
    }
}
