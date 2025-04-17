package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import java.util.Optional;

public record ReplaceEntriesStructure(
        String replacement,
        Optional<String> search,
        Optional<String> regexSearch,
        Optional<Integer> startRow,
        Optional<Integer> endRow,
        Optional<Integer> startColumn,
        Optional<Integer> endColumn
) implements IStructure {
}
