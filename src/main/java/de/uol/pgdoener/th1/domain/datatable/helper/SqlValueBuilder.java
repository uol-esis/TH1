package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import de.uol.pgdoener.th1.domain.datatable.model.SqlType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlValueBuilder {

    private final SqlTypeGuesser sqlTypeGuesser;
    private final SqlValueFormatter sqlValueFormatter;

    public List<Object[]> build(List<SqlColumn> columns, String[][] transformedMatrix) {
        String[][] valueMatrix = Arrays.copyOfRange(transformedMatrix, 1, transformedMatrix.length);
        List<Object[]> result = new ArrayList<>(transformedMatrix[0].length);

        for (String[] row : valueMatrix) {
            Object[] formattedRow = new Object[columns.size()];
            int j = 0;
            for (int i = 0; i < columns.size(); i++) {
                SqlColumn column = columns.get(i);
                if ("id".equals(column.getName()) && row.length < columns.size()) continue;

                String value = row[j++];
                SqlType detectedType = sqlTypeGuesser.guessType(value);
                column.mergeType(detectedType);

                formattedRow[i] = sqlValueFormatter.format(value, column.getType());
            }
            result.add(formattedRow);
        }
        return result;
    }
}
