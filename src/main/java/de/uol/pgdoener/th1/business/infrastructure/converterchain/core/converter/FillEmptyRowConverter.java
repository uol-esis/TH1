package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.FillEmptyRowStructure;

public class FillEmptyRowConverter extends Converter {
    private final FillEmptyRowStructure structure;

    public FillEmptyRowConverter(FillEmptyRowStructure structure) {
        this.structure = structure;
    }

    @Override
    public String[][] handleRequest(String[][] matrix) {
        String[] row = matrix[structure.rows()[0]];
        String lastNonEmptyValue = "";

        for (int i = 0; i < row.length; i++) {
            if (!row[i].trim().isEmpty()) {
                lastNonEmptyValue = row[i];
            } else {
                row[i] = lastNonEmptyValue;
            }
        }
        return super.handleRequest(matrix);
    }
}
