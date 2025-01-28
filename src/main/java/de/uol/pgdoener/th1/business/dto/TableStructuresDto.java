package de.uol.pgdoener.th1.business.dto;

import java.util.List;

public record TableStructuresDto(
        List<TableStructureSummaryDto> tableStructures
) {}