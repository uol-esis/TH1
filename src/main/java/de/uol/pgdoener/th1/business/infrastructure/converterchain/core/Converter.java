package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import lombok.NonNull;

/**
 * This is the super class for all converters in the converter chain.
 * Implementations of this class should implement the handleRequest method to process the matrix.
 * At the end of the method, it should call the handleRequest of this super class.
 */
public abstract class Converter {

    protected Converter nextConverter;

    /**
     * This method is called to process the matrix.
     * Implementations should override this method to provide their own processing logic.
     * The method should call the handleRequest of this super class at the end.
     * <p>
     * Implementations can modify the provided matrix as needed and return the modified matrix or
     * create a new matrix and return it.
     * Implementations can assume that the matrix is not null and has at least one row and one column.
     * Any exception thrown in this method will be caught, logged, and sent to the user.
     *
     * @param matrix the matrix to be processed
     * @return the processed matrix
     * @throws Exception if an error occurs during processing
     */
    public String[][] handleRequest(@NonNull String[][] matrix) throws Exception {
        if (matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Previous converter returned an empty matrix");
        }
        if (nextConverter != null) {
            return nextConverter.handleRequest(matrix);
        }
        return matrix;
    }

    public void setNextHandler(@NonNull Converter nextConverter) {
        this.nextConverter = nextConverter;
    }
}
