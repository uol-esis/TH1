package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

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
     *
     * @param matrix the matrix to be processed
     * @return the processed matrix
     */
    public String[][] handleRequest(String[][] matrix) {
        if (nextConverter != null) {
            return nextConverter.handleRequest(matrix);
        }
        return matrix;
    }

    public void setNextHandler(Converter nextConverter) {
        this.nextConverter = nextConverter;
    }
}
