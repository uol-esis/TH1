package de.uol.pgdoener.th1.infastructure.metabase;

/**
 * This exception indicates that an error occurred while interacting with the Metabase API.
 * The requested operation could not be completed due to an issue with the Metabase service.
 * However, if the request causing the metabase interaction was saving data in the database,
 * the database operation was successful. Thus, the request is not considered failed.
 */
public class MetabaseException extends RuntimeException {

    public MetabaseException(String message) {
        super(message);
    }

    public MetabaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
