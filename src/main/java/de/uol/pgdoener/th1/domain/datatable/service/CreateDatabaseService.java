package de.uol.pgdoener.th1.domain.datatable.service;

import de.uol.pgdoener.th1.domain.datatable.helper.SqlTableNameBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDatabaseService {
    private final DatabaseService databaseService;
    private final SqlTableNameBuilder sqlTableNameBuilder;
    private final DatabaseValidationService databaseValidationService;

    public void create(String mode, String originalName, String[][] transformedMatrix) {
        String tableName = sqlTableNameBuilder.build(originalName);

        switch (mode) {
            case "CREATE":
                databaseValidationService.checkIfTableAlreadyExist(tableName);
                databaseValidationService.checkSimilarity(tableName);
                databaseService.createDatabaseTableWithValues(tableName, transformedMatrix);
                return;
            case "EXTEND":
                databaseValidationService.checkIfTableDoesNotExist(tableName);
                databaseService.extendDatabaseTableWithValues(tableName, transformedMatrix);
                return;
            case "REPLACE":
                databaseValidationService.checkIfTableDoesNotExist(tableName);
                databaseService.replaceDatabaseTableWithValues(tableName, transformedMatrix);
                return;
            case "RESTRUCTURE":
                return;
            //databaseService.transformDatabaseTableWithValues(tableName, transformedMatrix);
            //return;
        }
    }

}
