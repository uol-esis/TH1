package de.uol.pgdoener.th1.domain.datatable.helper;


import java.util.regex.Pattern;

public abstract class SqlTypePattern {
    public static final Pattern INTEGER = Pattern.compile("\\d+");
    public static final Pattern DECIMAL = Pattern.compile("\\d+\\.\\d+");
    public static final Pattern BOOLEAN = Pattern.compile("true|false");
    public static final Pattern DATE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    public static final Pattern TIMESTAMP = Pattern.compile("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*");
    public static final Pattern UUID = Pattern.compile("[a-fA-F0-9]{8}-([a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}");
}
