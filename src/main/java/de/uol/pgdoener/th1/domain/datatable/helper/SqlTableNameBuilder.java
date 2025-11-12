package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.shared.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class SqlTableNameBuilder {

    private static final Pattern REMOVE_FILE_EXTENSION_PATTERN = Pattern.compile("\\.[^.]+$");
    private static final Pattern REPLACE_INVALID_CHARS_PATTERN = Pattern.compile("[^a-z0-9]+");
    private static final Pattern REPLACE_UNDERSCORES_PATTERN = Pattern.compile("(^_+)|(_+$)");
    private static final Pattern STARTS_WITH_NUMBER_PATTERN = Pattern.compile("^\\d.*");

    public String build(String originalName) {

        if (originalName == null || originalName.isBlank()) {
            throw new ServiceException(
                    "Conflict: The filename is missing",
                    HttpStatus.CONFLICT,
                    "The filename is missing or is blank",
                    "Try to upload a file with an existing name"
            );
        }

        String nameWithoutExtension = REMOVE_FILE_EXTENSION_PATTERN.matcher(originalName).replaceFirst("");

        String tableName = nameWithoutExtension
                .toLowerCase()
                .trim();
        tableName = REPLACE_INVALID_CHARS_PATTERN.matcher(tableName).replaceAll("_");
        tableName = REPLACE_UNDERSCORES_PATTERN.matcher(tableName).replaceAll("");

        if (STARTS_WITH_NUMBER_PATTERN.matcher(tableName).matches()) {
            tableName = "d" + tableName;
        }

        log.debug("Table name: {}", tableName);
        return tableName;
    }

}
