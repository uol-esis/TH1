package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.TransposeMatrixStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TransposeMatrixConverter extends Converter {

    private final TransposeMatrixStructureDto structure;

    /**
     * Transposes a 2D String array (matrix) by converting rows to columns and vice versa.
     * <p>
     * If rows have different lengths, missing values in the transposed matrix are filled with {@code null}.
     * <p>
     * This method first determines the maximum number of columns across all rows,
     * then inverts the matrix accordingly. The result is passed to the next handler in the chain.
     *
     * @param matrix the input 2D array to transpose
     * @return the transposed 2D array after passing to the superclass's handleRequest
     */
    @Override
    public String[][] handleRequest(String[][] matrix) {
        int maxCols = getMaxColumnCount(matrix);
        String[][] transposedMatrix = transposeMatrix(matrix, maxCols);
        return super.handleRequest(transposedMatrix);
    }

    // ----------------- Private helper methods ----------------- //

    /**
     * Determines the maximum number of columns across all rows in the given matrix.
     *
     * @param matrix the input 2D array
     * @return the maximum column count found in any row
     */
    private int getMaxColumnCount(String[][] matrix) {
        int maxCols = 0;
        for (String[] row : matrix) {
            if (row != null && row.length > maxCols) {
                maxCols = row.length;
            }
        }
        return maxCols;
    }

    /**
     * Transposes a 2D matrix by converting rows to columns and columns to rows.
     * Missing values (when rows have different lengths) are filled with {@code null}.
     *
     * @param matrix the matrix to transpose
     * @param maxCols the maximum number of columns used to define the transposed row size
     * @return the transposed matrix
     */
    private String[][] transposeMatrix(String[][] matrix, int maxCols) {
        String[][] transposedMatrix = new String[maxCols][matrix.length];

        for (int i = 0; i < maxCols; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j] != null && i < matrix[j].length) {
                    transposedMatrix[i][j] = matrix[j][i];
                } else {
                    transposedMatrix[i][j] = null;
                }
            }
        }

        return transposedMatrix;
    }
}
