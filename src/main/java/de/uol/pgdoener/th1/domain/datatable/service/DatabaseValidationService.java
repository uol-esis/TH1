package de.uol.pgdoener.th1.domain.datatable.service;

import de.uol.pgdoener.th1.domain.shared.exceptions.ServiceException;
import de.uol.pgdoener.th1.domain.datatable.helper.SimilarityChecker;
import de.uol.pgdoener.th1.infastructure.persistence.repository.DynamicTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseValidationService {

    private final DatabaseService databaseService;
    private final DynamicTableRepository dynamicTableRepository;
    private final SimilarityChecker similarityChecker;

    public void checkSimilarity(String tableName) {
        List<String> tableNames = databaseService.getTableNames();

        for (String existingTableNames : tableNames) {
            boolean check = similarityChecker.check(tableName, existingTableNames);
            if (check) {
                log.warn("A similar table with the name '{}' already exists.", tableName);
            }
        }
    }

    public void checkIfTableAlreadyExist(String tableName) {
        if (dynamicTableRepository.tableExists(tableName)) {
            throw new ServiceException(
                    "Conflict: The table already exists.",
                    HttpStatus.CONFLICT,
                    "A table with the name '" + tableName + "' already exists.",
                    "Use REPLACE to overwrite the table, EXTEND to add columns, or choose a different name."
            );
        }
    }

    public void checkIfTableDoesNotExist(String tableName) {
        if (!dynamicTableRepository.tableExists(tableName)) {
            throw new ServiceException(
                    "Not Found: The table does not exist.",
                    HttpStatus.NOT_FOUND,
                    "A table with the name '" + tableName + "' does not exist.",
                    "Use CREATE mode to create a new table with this name."
            );
        }
    }
}
