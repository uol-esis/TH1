package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.shared.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SqlTableNameBuilder {

    public String build(String originalName) {

        if (originalName == null || originalName.isBlank()) {
            throw new ServiceException(
                    "Conflict: The filename is missing",
                    HttpStatus.CONFLICT,
                    "The filename is missing or is blank",
                    "Try to upload a file with an existing name"
            );
        }

        String nameWithoutExtension = originalName.replaceFirst("\\.[^.]+$", "");

        String tableName = nameWithoutExtension
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");

        if (tableName.matches("\\d.*")) {
            tableName = "d" + tableName;
        }

        log.debug("Table name: {}", tableName);
        return tableName;
    }

}
