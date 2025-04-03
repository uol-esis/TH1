package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.HeaderRowStructure;

public class AddHeaderRowConverter extends Converter {
    private final HeaderRowStructure structure;

    public AddHeaderRowConverter(HeaderRowStructure structure) {
        this.structure = structure;
    }

    @Override
    public String[][] handleRequest(String[][] matrix) {

        String[] row = structure.headerRows();

        System.arraycopy(row, 0, matrix[0], 0, row.length);
        return super.handleRequest(matrix);
    }
}
