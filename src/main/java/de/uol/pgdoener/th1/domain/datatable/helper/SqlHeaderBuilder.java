package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import de.uol.pgdoener.th1.domain.datatable.model.SqlType;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SqlHeaderBuilder {

    private static final Pattern REMOVE_NON_ASCII_PATTERN = Pattern.compile("\\p{M}");
    private static final Pattern REPLACE_INVALID_CHARS_PATTERN = Pattern.compile("[^a-z0-9_]");
    private static final Pattern REPLACE_MULTIPLE_UNDERSCORES_PATTERN = Pattern.compile("_+");

    /**
     * Extracts the header from the first row of the matrix and infers SQL types based on the first data row.
     */
    public List<SqlColumn> build(String[][] matrix) {
        String[] headers = matrix[0];
        List<SqlColumn> columnHeaders = new ArrayList<>();

        columnHeaders.add(new SqlColumn("id",  SqlType.SERIAL_PRIMARY_KEY));

        for (String s : headers) {
            if (s.equals("id")) continue;
            String header = prepareForSQLColumnName(s);
            columnHeaders.add(new SqlColumn(header, SqlType.UNDEFINED));
        }
        return columnHeaders;
    }

    // private methods //

    private String prepareForSQLColumnName(String input) {
        if (input == null || input.isEmpty()) {
            return "_";
        }

        // Trim und Kleinschreibung
        String normalized = input.trim().toLowerCase();
        // Entfernt alle nicht-ASCII-Zeichen (z. B. Umlaute → ue)
        normalized = REMOVE_NON_ASCII_PATTERN.matcher(Normalizer.normalize(normalized, Normalizer.Form.NFD)).replaceAll("");
        // Ersetzt ungültige Zeichen durch Unterstrich
        normalized = REPLACE_INVALID_CHARS_PATTERN.matcher(normalized).replaceAll("_");
        // Entfernt doppelte Unterstriche
        normalized = REPLACE_MULTIPLE_UNDERSCORES_PATTERN.matcher(normalized).replaceAll("_");

        // Stellt sicher, dass der Name mit einem Buchstaben beginnt
        if (!Character.isLetter(normalized.charAt(0))) {
            normalized = "_" + normalized;
        }

        return normalized;
    }
}
