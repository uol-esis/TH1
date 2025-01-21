package de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.FillEmptyStructure;

public class FillEmptyCellsConverter extends Converter {
    private final FillEmptyStructure structure;

    public FillEmptyCellsConverter(FillEmptyStructure structure) {
        this.structure = structure;
    }

    @Override
    public String[][] handleRequest(String[][] matrix) {
        String[] row = matrix[structure.rows()[0]];
        String lastNonEmptyValue = "";

        for (int i = 0; i < row.length; i++) {
            if (!row[i].isEmpty()) {
                lastNonEmptyValue = row[i];
            } else {
                row[i] = lastNonEmptyValue;
            }
        }
        return super.handleRequest(matrix);
    }
}
