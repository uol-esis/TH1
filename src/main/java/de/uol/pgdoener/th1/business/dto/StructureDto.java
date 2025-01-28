package de.uol.pgdoener.th1.business.dto;

import de.uol.pgdoener.th1.business.enums.ConverterType;

import java.util.Optional;

public record StructureDto(
        ConverterType converterType,
        Optional<Integer[]> columns,
        Optional<Integer[]> rows,
        Optional<Integer> startRow,
        Optional<Integer> endRow,
        Optional<Integer> startColumn,
        Optional<Integer> endColumn
){}
