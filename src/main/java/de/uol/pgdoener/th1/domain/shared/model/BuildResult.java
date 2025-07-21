package de.uol.pgdoener.th1.domain.shared.model;

import de.uol.pgdoener.th1.application.dto.ReportDto;
import de.uol.pgdoener.th1.application.dto.ReportTypeDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.domain.tablestructure.builder.TableStructureBuilder;

import java.util.List;

/**
 * This is the result of an iteration of the {@link TableStructureBuilder#buildTableStructure(List)} method.
 * It contains the table structure built and the reports, which could not be resolved.
 * Additionally, it contains a boolean indicating if processing of the reports was ended early because an added
 * structure requires a new analysis of the matrix and the cause of the reanalysis.
 * The cause is null, if requiresReanalysis is false.
 *
 * @param tableStructure     the table structure built by this call
 * @param unresolvedReports  reports, which were not resolved by this call
 * @param requiresReanalysis true if processing was ended early, false otherwise
 * @param reanalysisCause    the cause of the reanalysis
 */
public record BuildResult(
        TableStructureDto tableStructure,
        List<ReportDto> unresolvedReports,
        boolean requiresReanalysis,
        ReportTypeDto reanalysisCause
) {
}
