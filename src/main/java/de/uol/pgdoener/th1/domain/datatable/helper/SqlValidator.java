package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import de.uol.pgdoener.th1.domain.shared.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SqlValidator {
    private static final int MAX_IDENTIFIER_LENGTH = 63;
    private static final Pattern VALID_IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private static final Set<String> RESERVED_SQL_KEYWORDS = Set.of(
            "select", "insert", "update", "delete", "drop", "create", "table", "from", "where", "join",
            "group", "order", "limit", "union", "alter", "into", "values"
    );

    public void validateHeaders(List<SqlColumn> headers) {
        for (SqlColumn header : headers) {
            validateIdentifier(header.getName(), "Column");
        }
    }

    public void validateTableName(String tableName) {
        validateIdentifier(tableName, "File");
    }

    //private methods//

    private void validateIdentifier(String identifier, String type) {

        if (identifier.length() > MAX_IDENTIFIER_LENGTH) {
            log.warn("{} name '{}' exceeds max length.", type, identifier);
            throw new ServiceException(
                    "The " + type + " name is too long.",
                    HttpStatus.BAD_REQUEST,
                    type + " name '" + identifier + "' exceeds max allowed length of " + MAX_IDENTIFIER_LENGTH + ".",
                    "Use a shorter " + type.toLowerCase() + " name."
            );
        }

        if (!VALID_IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            log.warn("{} name '{}' contains invalid characters.", type, identifier);
            throw new ServiceException(
                    "Invalid " + type + " name.",
                    HttpStatus.BAD_REQUEST,
                    type + " name '" + identifier + "' contains invalid characters.",
                    "Use only letters, numbers, and underscores, starting with a letter or underscore."
            );
        }

        if (RESERVED_SQL_KEYWORDS.contains(identifier.toLowerCase())) {
            log.warn("{} name '{}' is a reserved keyword.", type, identifier);
            throw new ServiceException(
                    type + " name cannot be a SQL keyword.",
                    HttpStatus.BAD_REQUEST,
                    type + " name '" + identifier + "' is a reserved SQL keyword.",
                    "Choose a different name that is not a SQL keyword."
            );
        }
    }

}
