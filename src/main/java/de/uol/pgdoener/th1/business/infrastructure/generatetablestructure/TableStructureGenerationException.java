package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

/**
 * This exception might be thrown at any point while generating a Table Structure
 */
public class TableStructureGenerationException extends RuntimeException {

    public TableStructureGenerationException(String message) {
        super(message);
    }

    public TableStructureGenerationException(String message, Exception cause) {
        super(message, cause);
    }

}
