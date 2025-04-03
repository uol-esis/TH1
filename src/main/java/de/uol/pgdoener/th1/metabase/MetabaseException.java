package de.uol.pgdoener.th1.metabase;

public class MetabaseException extends RuntimeException {

    public MetabaseException(String message) {
        super(message);
    }

    public MetabaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
