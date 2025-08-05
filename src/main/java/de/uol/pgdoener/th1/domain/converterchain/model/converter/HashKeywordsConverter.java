package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.infastructure.persistence.entity.HashKeywordsStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class HashKeywordsConverter extends Converter {

    private final HashKeywordsStructure structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        Pattern pattern = buildPattern();
        String[] headers = matrix[0];

        List<Integer> matchingColumns = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            String header = headers[0];
            if (pattern.matcher(header).find()) {
                matchingColumns.add(i);
            }
        }

        if (matchingColumns.isEmpty()) {
            log.info("No keywords matching found");
            return matrix;
        }

        for (int i = 1; i < matrix.length; i++) {
            StringBuilder sb = new StringBuilder();

            for (int col : matchingColumns) {
                sb.append(matrix[i][col]);
            }
            String combined = sb.toString();
            String hashed = Integer.toHexString(combined.hashCode());

            int firstCol = matchingColumns.getFirst();
            matrix[i][firstCol] = hashed;

            for (int j = 1; j < matchingColumns.size(); j++) {
                matrix[i][matchingColumns.get(j)] = "";
            }
        }

        return super.handleRequest(matrix);
    }

    private Pattern buildPattern() {
        if (structure.getKeywords() == null) {
            return Pattern.compile("a^"); // never matches
        }
        List<String> escapedKeywords = new ArrayList<>();
        for (String kw : structure.getKeywords()) {
            if (kw != null) {
                escapedKeywords.add(Pattern.quote(kw));
            }
        }
        String joined = String.join("|", escapedKeywords);
        String regex = switch (structure.getMatchType()) {
            case CONTAINS -> ".*(" + joined + ").*";
            case EQUALS -> "^(" + joined + ")$";
            default -> {
                log.warn("Unknown matchType '{}', defaulting to CONTAINS", structure.getMatchType());
                yield ".*(" + joined + ").*";
            }
        };
        int flags = structure.getIgnoreCase() ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        return Pattern.compile(regex, flags);
    }
}
