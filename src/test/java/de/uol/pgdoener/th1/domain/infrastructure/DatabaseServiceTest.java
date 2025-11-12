package de.uol.pgdoener.th1.domain.infrastructure;

import de.uol.pgdoener.th1.domain.datatable.service.DatabaseService;
import de.uol.pgdoener.th1.domain.shared.exceptions.ServiceException;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class DatabaseServiceTest {

    @Autowired
    private DSLContext dsl;

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private DatabaseService databaseService;

    private String[][] simpleMatrix;

    @BeforeEach
    void setup() {
        simpleMatrix = new String[][]{
                {"id", "name", "Alter"},
                {"1", "Alice", "10"},
                {"2", "Bob", "20"}
        };
    }

    @Test
    void testValidTableCreation() {
        String tableName = "valid_table";

        databaseService.createDatabaseTableWithValues(tableName, simpleMatrix);

        List<String> tableNames = databaseService.getTableNames();

        assertThat(tableNames).contains(tableName);
    }

    @Test
    void testSqlInjectionInTableName() {
        String maliciousTable = "users; DROP TABLE important_data; --";

        assertThatThrownBy(() ->
                databaseService.createDatabaseTableWithValues(maliciousTable, simpleMatrix)
        ).isInstanceOf(ServiceException.class).hasMessageContaining("Invalid table name");
    }

    @Test
    void testSqlInjectionInColumnNameDoesNotDropAnything() {
        String tableName = "bad_table";
        String[][] matrix = {
                {"id", "name; DROP TABLE valid_table; --"},
                {"1", "Eve"}
        };

        databaseService.createDatabaseTableWithValues("valid_table", simpleMatrix);
        databaseService.createDatabaseTableWithValues(tableName, matrix);
        assertThat(databaseService.tableExists("valid_table")).isTrue();

    }

    @Test
    void testInvalidColumnNameStartingWithNumber() {
        String[][] matrix = {
                {"1column", "validName"},
                {"1", "value"}
        };

        databaseService.createDatabaseTableWithValues("invalid_column", matrix);

        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "invalid_column")
                .getValues("column_name", String.class);

        assertThat(columnNames).contains("col_1column", "validname");
    }

    @Test
    void testInvalidColumnNameStartingWithSpace() {
        String[][] matrix = {
                {"column name", "valid Name"},
                {"1", "value"}
        };

        databaseService.createDatabaseTableWithValues("invalid_column_2", matrix);

        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "invalid_column_2")
                .getValues("column_name", String.class);

        assertThat(columnNames).contains("column_name", "valid_name");
    }

    @Test
    void testInvalidCharactersInTableName() {
        String invalidTable = "table-with-dash!";

        assertThatThrownBy(() ->
                databaseService.createDatabaseTableWithValues(invalidTable, simpleMatrix)
        ).isInstanceOf(ServiceException.class);
    }

    @Test
    void testVeryLongTableName() {
        String longName = "t".repeat(300); // weit über 63 Zeichen (Postgres Limit)

        assertThatThrownBy(() ->
                databaseService.createDatabaseTableWithValues(longName, simpleMatrix)
        ).isInstanceOf(ServiceException.class);
    }

    @Test
    void testEmptyColumnName() {
        String[][] matrix = {
                {"id", ""},
                {"1", "value"}
        };

        databaseService.createDatabaseTableWithValues("empty_column_table", matrix);

        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "empty_column_table")
                .getValues("column_name", String.class);

        assertThat(columnNames).contains("id");
        assertThat(columnNames).contains("col_unknown");
    }

    @Test
    void testStarColumnName() {
        String[][] matrix = {
                {"id", "*"},
                {"1", "value"}
        };

        databaseService.createDatabaseTableWithValues("empty_column_table", matrix);

        assertThat(databaseService.tableExists("empty_column_table")).isTrue();

        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "empty_column_table")
                .getValues("column_name", String.class);

        assertThat(columnNames).contains("id");
        assertThat(columnNames).contains("col_unknown");

    }

    @Test
    void testDuplicateColumnNames() {
        String[][] matrix = {
                {"id", "id"},
                {"1", "duplicate"}
        };

        assertThatThrownBy(() ->
                databaseService.createDatabaseTableWithValues("duplicate_column_table", matrix)
        ).isInstanceOf(BadSqlGrammarException.class);
    }

    @Test
    void testUmlauteColumnNames() {
        String[][] matrix = {
                {"id", "Städte", "stadt teil"},
                {"1", "oldenburg", ""}
        };

        databaseService.createDatabaseTableWithValues("umlaut_table", matrix);
        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "umlaut_table")
                .getValues("column_name", String.class);
    }

    @Test
    void testMatrixWithoutHeader() {
        String[][] matrix = {{"1", "Alice"}};


        databaseService.createDatabaseTableWithValues("no_header_table", matrix);

        assertThat(databaseService.tableExists("no_header_table")).isTrue();

        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "no_header_table")
                .getValues("column_name", String.class);

        assertThat(columnNames).contains("col_1");
        assertThat(columnNames).contains("alice");
    }

    @Test
    void testReservedKeywordAsColumnName() {
        String[][] matrix = {
                {"select", "from"},
                {"1", "test"}
        };

        databaseService.createDatabaseTableWithValues("reserved_keywords_table", matrix);

        List<String> columnNames = dsl.fetch("SELECT column_name FROM information_schema.columns WHERE table_name = ?", "reserved_keywords_table")
                .getValues("column_name", String.class);

        assertThat(columnNames).contains("select", "from");
    }

    @Test
    void testVeryLongColumnName() {
        String longName = "c".repeat(1000);

        String[][] matrix = {
                {longName, "valid"},
                {"1", "test"}
        };

        databaseService.createDatabaseTableWithValues("long_column_table", matrix);

        List<String> columnNames = dsl.fetch(
                "SELECT column_name FROM information_schema.columns WHERE table_name = ?",
                "long_column_table"
        ).getValues("column_name", String.class);

        // Name ist gekürzt auf ≤ 63 Zeichen
        assertThat(columnNames.getFirst().length()).isLessThanOrEqualTo(63);
        assertThat(columnNames).contains("valid");
    }
}
