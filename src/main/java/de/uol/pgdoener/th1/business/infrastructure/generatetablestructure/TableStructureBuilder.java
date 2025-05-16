package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TableStructureBuilder {

    @Getter
    private final TableStructureDto tableStructure;

    public TableStructureBuilder(TableStructureGenerationSettingsDto settings) {
        tableStructure = new TableStructureDto();
        tableStructure.setName("");
        settings.getRemoveHeader().ifPresent(s -> {
            if (s.isEnabled().orElse(false)) {
                buildRemoveHeaderStructure(s.getThreshold().orElse(2), s.getBlockList());
            }
        });
        buildRemoveFooterStructure();
        buildRemoveTrailingColumnStructure();
    }

    /**
     * Constructs the full table structure with necessary converters applied.
     */
    public Pair<TableStructureDto, List<ReportDto>> buildTableStructure(List<ReportDto> reports) {
        List<ReportDto> unresolvedReports = new ArrayList<>();
        for (ReportDto report : reports) {
            switch (report) {
                case GroupedHeaderReportDto r -> buildGroupHeaderStructure(r);
                case ColumnTypeMismatchReportDto r -> unresolvedReports.add(r);
                // TODO remove default branch
                default -> throw new IllegalStateException("Unexpected value: " + report);
            }
        }

        return Pair.of(tableStructure, unresolvedReports);
    }

    /**
     * Builds converter structure for removing header rows.
     */
    private void buildRemoveHeaderStructure() {
        log.debug("Start buildRemoveHeaderStructure");
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto();
        removeHeaderStructure.setConverterType(ConverterTypeDto.REMOVE_HEADER);
        log.debug("Finish buildRemoveHeaderStructure");
        tableStructure.addStructuresItem(removeHeaderStructure);
    }

    /**
     * Builds converter structure for removing footer rows.
     */
    private void buildRemoveFooterStructure() {
        log.debug("Start buildRemoveFooterStructure");
        RemoveFooterStructureDto removeFooterStructure = new RemoveFooterStructureDto();
        removeFooterStructure.setConverterType(ConverterTypeDto.REMOVE_HEADER);
        log.debug("Finish buildRemoveFooterStructure");
        tableStructure.addStructuresItem(removeFooterStructure);
    }

    /**
     * Builds converter structure for removing trailing column.
     */
    private void buildRemoveTrailingColumnStructure() {
        log.debug("Start buildRemoveTrailingColumnStructure");
        RemoveTrailingColumnStructureDto removeTrailingColumnStructure = new RemoveTrailingColumnStructureDto();
        removeTrailingColumnStructure.setConverterType(ConverterTypeDto.REMOVE_TRAILING_COLUMN);
        log.debug("Finish buildRemoveTrailingColumnStructure");
        tableStructure.addStructuresItem(removeTrailingColumnStructure);
    }


    /**
     * Builds converter structure for removing invalid rows.
     */
    private void buildRemoveRowsStructure(TableStructureDto tableStructureDto, int threshold, List<String> blockList) {
        log.debug("Start buildRemoveRowsStructure");
        RemoveInvalidRowsStructureDto removeInvalidRowStructure = new RemoveInvalidRowsStructureDto();
        removeInvalidRowStructure.setConverterType(ConverterTypeDto.REMOVE_INVALID_ROWS);
        removeInvalidRowStructure.threshold(threshold)
                .blackList(blockList);
        log.debug("Finish buildRemoveRowsStructure");
        tableStructure.addStructuresItem(removeInvalidRowStructure);
    }

    /**
     * Builds converter structure for removing grouped header rows.
     */
    private void buildGroupHeaderStructure(GroupedHeaderReportDto reportDto) {
        log.debug("Start buildGroupHeaderStructure");
        RemoveGroupedHeaderStructureDto groupHeaderStructure = new RemoveGroupedHeaderStructureDto();
        groupHeaderStructure.setConverterType(ConverterTypeDto.REMOVE_GROUPED_HEADER);
        groupHeaderStructure.setColumnIndex(reportDto.getColumnIndex());
        groupHeaderStructure.setRowIndex(reportDto.getRowIndex());
        groupHeaderStructure.setStartRow(Optional.of(reportDto.getStartRow()));
        log.debug("Finish buildGroupHeaderStructure");
        tableStructure.addStructuresItem(groupHeaderStructure);
    }

    /**
     * Builds converter structure for setting header names.
     */
    private void buildHeaderNameStructure(List<String> headerNames) {
        log.debug("Start buildHeaderNameStructure");
        AddHeaderNameStructureDto addHeaderNamesStructure = new AddHeaderNameStructureDto();
        addHeaderNamesStructure.setConverterType(ConverterTypeDto.ADD_HEADER_NAME);
        addHeaderNamesStructure.setHeaderNames(headerNames);
        log.debug("Finish buildHeaderNameStructure");
        this.tableStructure.addStructuresItem(addHeaderNamesStructure);
    }

    /**
     * Builds converter structure to fill partially filled rows.
     */
    private void buildFillEmptyRowStructure(List<Integer> rowsToFill) {
        log.debug("Start buildFillEmptyRowStructure");
        FillEmptyRowStructureDto fillEmptyRowStructure = new FillEmptyRowStructureDto();
        fillEmptyRowStructure.setConverterType(ConverterTypeDto.FILL_EMPTY_ROW);
        fillEmptyRowStructure.setRowIndex(rowsToFill);
        log.debug("Finish buildFillEmptyRowStructure");
        tableStructure.addStructuresItem(fillEmptyRowStructure);
    }

}
