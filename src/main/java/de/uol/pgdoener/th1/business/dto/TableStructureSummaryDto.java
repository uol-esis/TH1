package de.uol.pgdoener.th1.business.dto;

import java.util.List;

public record TableStructureSummaryDto(
        Long id,
        String name,
        List<StructureSummaryDto> structure
) {}