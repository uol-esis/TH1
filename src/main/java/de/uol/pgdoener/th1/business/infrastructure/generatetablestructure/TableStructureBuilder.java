package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class TableStructureBuilder {

    private final TableStructureDto tableStructure;

    /**
     * This creates a new builder for table structures.
     * This constructor adds converters enabled by default in the generation settings.
     *
     * @param settings settings for generating a table structure
     */
    public TableStructureBuilder(TableStructureGenerationSettingsDto settings) {
        tableStructure = new TableStructureDto();
        tableStructure.setName("");
        RemoveHeaderSettingsDto removeHeaderSettings = settings.getRemoveHeader().orElse(new RemoveHeaderSettingsDto());
        if (removeHeaderSettings.isEnabled()) {
            buildRemoveHeaderStructure(removeHeaderSettings);
        }
        RemoveFooterSettingsDto removeFooterSettings = settings.getRemoveFooter().orElse(new RemoveFooterSettingsDto());
        if (removeFooterSettings.isEnabled()) {
            buildRemoveFooterStructure(removeFooterSettings);
        }
        RemoveColumnsSettingsDto removeColumnsSettings = settings.getRemoveColumns().orElse(new RemoveColumnsSettingsDto());
        if (removeColumnsSettings.isEnabled()) {
            buildRemoveTrailingColumnStructure(removeColumnsSettings);
        }

        RemoveInvalidRowsSettingsDto removeInvalidRowsSettings = settings.getRemoveInvalidRows().orElse(new RemoveInvalidRowsSettingsDto());
        if (removeInvalidRowsSettings.isEnabled()) {
            buildRemoveInvalidRowsStructure(removeInvalidRowsSettings);
        }
    }

    /**
     * This method iterates over the provided reports and adds structures to the table structure
     */
    public BuildResult buildTableStructure(List<ReportDto> reports) {
        List<ReportDto> unresolvedReports = new ArrayList<>();
        boolean earlyBreak = false;
        ReportTypeDto reanalysisCause = null;

        reportsLoop:
        for (ReportDto report : reports) {
            switch (report) {
                case GroupedHeaderReportDto r -> {
                    buildFillEmptyRowStructure(r);
                    buildGroupHeaderStructure(r);
                    // break since no other reports should be acted upon after the removal of the grouped header
                    earlyBreak = true;
                    reanalysisCause = ReportTypeDto.GROUPED_HEADER;
                    break reportsLoop;
                }
                case ColumnTypeMismatchReportDto r -> unresolvedReports.add(r);
                case EmptyColumnReportDto r -> unresolvedReports.add(r);
                case EmptyRowReportDto r -> unresolvedReports.add(r);
                case EmptyHeaderReportDto r -> unresolvedReports.add(r);
                case MergeableColumnsReportDto r -> unresolvedReports.add(r);
                case SameAsHeaderReportDto r -> unresolvedReports.add(r);
                case MissingEntryReportDto r -> unresolvedReports.add(r);
                case SplitRowReportDto r -> unresolvedReports.add(r);
            }
        }

        return new BuildResult(
                tableStructure,
                unresolvedReports,
                earlyBreak,
                reanalysisCause
        );
    }

    /**
     * Builds converter structure for removing header rows.
     */
    private void buildRemoveHeaderStructure(RemoveHeaderSettingsDto settings) {
        log.debug("Start buildRemoveHeaderStructure");
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto();
        removeHeaderStructure.converterType(ConverterTypeDto.REMOVE_HEADER)
                .threshold(settings.getThreshold())
                .setBlackList(settings.getBlockList());
        log.debug("Finish buildRemoveHeaderStructure");
        tableStructure.addStructuresItem(removeHeaderStructure);
    }

    /**
     * Builds converter structure for removing footer rows.
     */
    private void buildRemoveFooterStructure(RemoveFooterSettingsDto settings) {
        log.debug("Start buildRemoveFooterStructure");
        RemoveFooterStructureDto removeFooterStructure = new RemoveFooterStructureDto();
        removeFooterStructure.converterType(ConverterTypeDto.REMOVE_HEADER)
                .threshold(settings.getThreshold())
                .setBlackList(settings.getBlockList());
        log.debug("Finish buildRemoveFooterStructure");
        tableStructure.addStructuresItem(removeFooterStructure);
    }

    /**
     * Builds converter structure for removing trailing column.
     */
    private void buildRemoveTrailingColumnStructure(RemoveColumnsSettingsDto settings) {
        log.debug("Start buildRemoveTrailingColumnStructure");
        RemoveTrailingColumnStructureDto removeTrailingColumnStructure = new RemoveTrailingColumnStructureDto();
        removeTrailingColumnStructure.converterType(ConverterTypeDto.REMOVE_TRAILING_COLUMN)
                .threshold(settings.getThreshold())
                .blackList(settings.getBlockList());
        log.debug("Finish buildRemoveTrailingColumnStructure");
        tableStructure.addStructuresItem(removeTrailingColumnStructure);
    }


    /**
     * Builds converter structure for removing invalid rows.
     */
    private void buildRemoveInvalidRowsStructure(RemoveInvalidRowsSettingsDto settings) {
        log.debug("Start buildRemoveInvalidRowsStructure");
        RemoveInvalidRowsStructureDto removeInvalidRowStructure = new RemoveInvalidRowsStructureDto();
        removeInvalidRowStructure.converterType(ConverterTypeDto.REMOVE_INVALID_ROWS)
                .threshold(settings.getThreshold())
                .blackList(settings.getBlockList());
        log.debug("Finish buildRemoveInvalidRowsStructure");
        tableStructure.addStructuresItem(removeInvalidRowStructure);
    }

    /**
     * Builds converter structure for removing grouped header rows.
     */
    private void buildGroupHeaderStructure(GroupedHeaderReportDto reportDto) {
        log.debug("Start buildGroupHeaderStructure");
        RemoveGroupedHeaderStructureDto groupHeaderStructure = new RemoveGroupedHeaderStructureDto();
        groupHeaderStructure.converterType(ConverterTypeDto.REMOVE_GROUPED_HEADER)
                .columnIndex(reportDto.getColumnIndex())
                .rowIndex(reportDto.getRowIndex())
                .startRow(reportDto.getStartRow())
                .startColumn(reportDto.getStartColumn());
        log.debug("Finish buildGroupHeaderStructure");
        tableStructure.addStructuresItem(groupHeaderStructure);
    }

    /**
     * Builds converter structure for setting header names.
     */
    private void buildHeaderNameStructure(List<String> headerNames) {
        log.debug("Start buildHeaderNameStructure");
        AddHeaderNameStructureDto addHeaderNamesStructure = new AddHeaderNameStructureDto();
        addHeaderNamesStructure.converterType(ConverterTypeDto.ADD_HEADER_NAME)
                .headerNames(headerNames);
        log.debug("Finish buildHeaderNameStructure");
        this.tableStructure.addStructuresItem(addHeaderNamesStructure);
    }

    /**
     * Builds converter structure to fill partially filled rows.
     */
    private void buildFillEmptyRowStructure(GroupedHeaderReportDto reportDto) {
        log.debug("Start buildFillEmptyRowStructure");
        FillEmptyRowStructureDto fillEmptyRowStructure = new FillEmptyRowStructureDto();
        fillEmptyRowStructure.converterType(ConverterTypeDto.FILL_EMPTY_ROW)
                .rowIndex(reportDto.getRowIndex());
        log.debug("Finish buildFillEmptyRowStructure");
        tableStructure.addStructuresItem(fillEmptyRowStructure);
    }

}
