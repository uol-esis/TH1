package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.MergeColumnsStructureDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MergeColumnsConverter extends Converter {

    private final MergeColumnsStructureDto structure;

    // the precedenceOrder list should only be modified by the fillPrecedenceOrder method
    private List<Integer> precedenceOrder;

    @Override
    public String[][] handleRequest(@NonNull String[][] matrix) {
        this.precedenceOrder = structure.getPrecedenceOrder();
        validateInputs();
        fillPrecedenceOrder();

        // Merge columns row by row
        for (int i = 0; i < matrix.length; i++) {
            String[] row = matrix[i];
            String[] newRow = constructMergedRow(row);
            matrix[i] = newRow;
        }

        // Set the header name for the merged column
        int mergedColumnIndex = mergedColumnIndex();
        String headerName = structure.getHeaderName();
        matrix[0][mergedColumnIndex] = headerName;

        return super.handleRequest(matrix);
    }


    private void validateInputs() {
        List<Integer> columns = structure.getColumnIndex();
        if (columns.size() < 2) {
            throwConverterException("At least two columns must be specified for merging.");
        }
        for (Integer index : precedenceOrder) {
            if (!columns.contains(index)) {
                throwConverterException("Precedence order must only contain columns that are specified for merging.");
            }
        }
    }

    /**
     * If there are not all columns in the precedence order, append the rest in ascending order at the end.
     * Also make the list unmodifiable to prevent further changes.
     */
    private void fillPrecedenceOrder() {
        List<Integer> columns = new ArrayList<>(structure.getColumnIndex());
        List<Integer> po = new ArrayList<>(precedenceOrder);
        Collections.sort(columns);
        for (Integer column : columns) {
            if (!po.contains(column)) {
                po.add(column);
            }
        }
        precedenceOrder = Collections.unmodifiableList(po);
    }

    /**
     * Constructs a new row by merging the specified columns.
     * The merged column has the lowest index in the precedence order.
     * Entries are taken from the columns in the precedence order.
     * The first valid entry is used.
     *
     * @param row the row to merge
     * @return the new row with merged columns
     */
    private String[] constructMergedRow(String[] row) {
        String[] newRow = new String[row.length - precedenceOrder.size() + 1];
        int newRowIndex = 0;
        int mergedColumnIndex = mergedColumnIndex();

        for (int i = 0; i < row.length; i++) {
            if (i == mergedColumnIndex) {
                for (Integer index : precedenceOrder) {
                    String value = row[index];
                    if (isValid(value)) {
                        newRow[mergedColumnIndex] = value;
                        break;
                    }
                }
                if (newRow[mergedColumnIndex] == null) {
                    newRow[mergedColumnIndex] = "";
                }
                newRowIndex++;
            } else if (!precedenceOrder.contains(i)) {
                newRow[newRowIndex] = row[i];
                newRowIndex++;
            }
        }

        return newRow;
    }

    private boolean isValid(String string) {
        return string != null && !string.isBlank();
    }

    private int mergedColumnIndex() {
        // There must be an index. This was checked before.
        return precedenceOrder.stream().min(Integer::compareTo).orElseThrow();
    }

}
