package de.uol.pgdoener.th1.business.dto;

import java.util.List;

public record TableStructureDto(
        Long id,
        String name,
        char delimiter,
        List<StructureDto> structure,
        int endRow,
        int endColumn
) {}
