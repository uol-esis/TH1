package de.uol.pgdoener.th1.application.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.application.dto.*;
import de.uol.pgdoener.th1.application.infrastructure.BuildResult;
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
        RemoveTrailingColumnSettingsDto removeTrailingColumnSettings = settings.getRemoveTrailingColumn()
                .orElse(new RemoveTrailingColumnSettingsDto());
        if (removeTrailingColumnSettings.isEnabled()) {
            buildRemoveTrailingColumnStructure(removeTrailingColumnSettings);
        }
        RemoveLeadingColumnSettingsDto removeLeadingColumnSettingsDto = settings.getRemoveLeadingColumn()
                .orElse(new RemoveLeadingColumnSettingsDto());
        if (removeLeadingColumnSettingsDto.isEnabled()) {
            buildRemoveLeadingColumnStructure(removeLeadingColumnSettingsDto);
        }
        RemoveInvalidRowsSettingsDto removeInvalidRowsSettings = settings.getRemoveInvalidRows().orElse(new RemoveInvalidRowsSettingsDto());
        if (removeInvalidRowsSettings.isEnabled()) {
            buildRemoveInvalidRowsStructure(removeInvalidRowsSettings);
        }
    }

    /**
     * This method iterates over the provided reports and adds structures to the table structure
     */
    public BuildResult buildTableStructure(List<ReportDto> reports, TableStructureGenerationSettingsDto settings) {
        List<ReportDto> unresolvedReports = new ArrayList<>();
        boolean earlyBreak = false;
        ReportTypeDto reanalysisCause = null;

        reportsLoop:
        for (ReportDto report : reports) {
            switch (report) {
                case GroupedHeaderReportDto r -> {
                    if (!r.getRowsToFill().isEmpty()) {
                        buildFillEmptyRowStructure(r);
                    }
                    if (!r.getColumnsToFill().isEmpty()) {
                        buildFillEmptyColumnStructure(r);
                    }
                    buildGroupHeaderStructure(r);
                    buildHeaderNameStructure(r.getHeaderNames());
                    // break since no other reports should be acted upon after the removal of the grouped header
                    earlyBreak = true;
                    reanalysisCause = ReportTypeDto.GROUPED_HEADER;
                    break reportsLoop;
                }
                case ColumnTypeMismatchReportDto r -> {
                    List<ColumnTypeMismatchDto> unresolvedMismatches = new ArrayList<>();

                    for (ColumnTypeMismatchDto mismatch : r.getMismatches()) {
                        if (mismatch.getReplacementValue().isEmpty()) {
                            unresolvedMismatches.add(mismatch);
                        } else {
                            buildReplaceEntriesStructureFromColumnTypeMismatch(mismatch);
                        }
                    }

                    if (!unresolvedMismatches.isEmpty()) {
                        unresolvedReports.add(new ColumnTypeMismatchReportDto().mismatches(unresolvedMismatches));
                    }
                }
                case MergeableColumnsReportDto r -> {
                    buildMergeableColumnsStructureFromColumnTypeMismatch(r);
                    earlyBreak = true;
                    reanalysisCause = ReportTypeDto.MERGEABLE_COLUMNS;
                    break reportsLoop;
                }
                case SumReportDto r -> {
                    RemoveKeywordsSettingsDto removeKeywordsSettingsDto = settings.getRemoveKeywords().orElse(new RemoveKeywordsSettingsDto());
                    if (removeKeywordsSettingsDto.isEnabled()) {
                        buildRemoveKeywordStructure(removeKeywordsSettingsDto);
                    } else {
                        unresolvedReports.add(r);
                        // earlyBreak = true;
                        // reanalysisCause = ReportTypeDto.SUM;
                        // break reportsLoop;
                    }
                }
                case EmptyColumnReportDto r -> unresolvedReports.add(r);
                case EmptyRowReportDto r -> unresolvedReports.add(r);
                case EmptyHeaderReportDto r -> unresolvedReports.add(r);
                case SameAsHeaderReportDto r -> unresolvedReports.add(r);
                case MissingEntryReportDto r -> unresolvedReports.add(r);
                case SplitRowReportDto r -> {
                    buildSplitRowReportDto(r);
                    earlyBreak = true;
                    reanalysisCause = ReportTypeDto.SPLIT_ROW;
                    break reportsLoop;
                }
                case TransposeMatrixReportDto r -> unresolvedReports.add(r);
            }
        }

        return new BuildResult(
                tableStructure,
                unresolvedReports,
                earlyBreak,
                reanalysisCause
        );
    }

    private void buildReplaceEntriesStructureFromColumnTypeMismatch(ColumnTypeMismatchDto mismatch) {
        log.debug("Start buildReplaceEntriesStructure for column index {}", mismatch.getColumnIndex());
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .converterType(ConverterTypeDto.REPLACE_ENTRIES)
                .columnIndex(mismatch.getColumnIndex())
                .startRow(1)
                .search(mismatch.getReplacementSearch().orElseThrow())
                .replacement(mismatch.getReplacementValue().orElseThrow());
        log.debug("Finish buildReplaceEntriesStructure for column index {}", mismatch.getColumnIndex());
        tableStructure.addStructuresItem(structure);
    }

    private void buildMergeableColumnsStructureFromColumnTypeMismatch(MergeableColumnsReportDto report) {
        log.debug("Start buildMergeableColumnsStructure for column index");
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto()
                .converterType(ConverterTypeDto.MERGE_COLUMNS)
                .columnIndex(report.getMergeables());
        log.debug("Finish buildMergeableColumnsStructure for column index");
        tableStructure.addStructuresItem(structure);
    }

    /**
     * Builds converter structure for removing header rows.
     */
    private void buildSplitRowReportDto(SplitRowReportDto report) {
        log.debug("Start buildSplitRowStructure");
        SplitRowStructureDto SplitRowStructure = new SplitRowStructureDto();
        SplitRowStructure.converterType(ConverterTypeDto.SPLIT_ROW)
                .columnIndex(report.getColumnIndex())
                .delimiter(report.getDelimiter());
        log.debug("Finish buildSplitRowStructure");
        tableStructure.addStructuresItem(SplitRowStructure);
    }

    /**
     * Builds converter structure for removing header rows.
     */
    private void buildRemoveHeaderStructure(RemoveHeaderSettingsDto settings) {
        log.debug("Start buildRemoveHeaderStructure");
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto();
        removeHeaderStructure.converterType(ConverterTypeDto.REMOVE_HEADER)
                .threshold(settings.getThreshold())
                .setBlockList(settings.getBlockList());
        log.debug("Finish buildRemoveHeaderStructure");
        tableStructure.addStructuresItem(removeHeaderStructure);
    }

    /**
     * Builds converter structure for removing footer rows.
     */
    private void buildRemoveFooterStructure(RemoveFooterSettingsDto settings) {
        log.debug("Start buildRemoveFooterStructure");
        RemoveFooterStructureDto removeFooterStructure = new RemoveFooterStructureDto();
        removeFooterStructure.converterType(ConverterTypeDto.REMOVE_FOOTER)
                .threshold(settings.getThreshold())
                .setBlockList(settings.getBlockList());
        log.debug("Finish buildRemoveFooterStructure");
        tableStructure.addStructuresItem(removeFooterStructure);
    }

    /**
     * Builds converter structure for removing trailing column.
     */
    private void buildRemoveTrailingColumnStructure(RemoveTrailingColumnSettingsDto settings) {
        log.debug("Start buildRemoveTrailingColumnStructure");
        RemoveTrailingColumnStructureDto removeTrailingColumnStructure = new RemoveTrailingColumnStructureDto();
        removeTrailingColumnStructure.converterType(ConverterTypeDto.REMOVE_TRAILING_COLUMN)
                .blockList(settings.getBlockList());
        log.debug("Finish buildRemoveTrailingColumnStructure");
        tableStructure.addStructuresItem(removeTrailingColumnStructure);
    }

    /**
     * Builds converter structure for removing leading column.
     */
    private void buildRemoveLeadingColumnStructure(RemoveLeadingColumnSettingsDto settings) {
        log.debug("Start buildRemoveLeadingColumnStructure");
        RemoveLeadingColumnStructureDto removeLeadingColumnStructure = new RemoveLeadingColumnStructureDto();
        removeLeadingColumnStructure.converterType(ConverterTypeDto.REMOVE_TRAILING_COLUMN)
                .blockList(settings.getBlockList());
        log.debug("Finish buildRemoveLeadingColumnStructure");
        tableStructure.addStructuresItem(removeLeadingColumnStructure);
    }


    /**
     * Builds converter structure for removing invalid rows.
     */
    private void buildRemoveInvalidRowsStructure(RemoveInvalidRowsSettingsDto settings) {
        log.debug("Start buildRemoveInvalidRowsStructure");
        RemoveInvalidRowsStructureDto removeInvalidRowStructure = new RemoveInvalidRowsStructureDto();
        removeInvalidRowStructure.converterType(ConverterTypeDto.REMOVE_INVALID_ROWS)
                .threshold(settings.getThreshold())
                .blockList(settings.getBlockList());
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
                .rowIndex(reportDto.getRowsToFill());
        log.debug("Finish buildFillEmptyRowStructure");
        tableStructure.addStructuresItem(fillEmptyRowStructure);
    }

    /**
     * Builds converter structure to fill partially filled rows.
     */
    private void buildFillEmptyColumnStructure(GroupedHeaderReportDto reportDto) {
        log.debug("Start buildFillEmptyColumnStructure");
        FillEmptyColumnStructureDto fillEmptyColumnStructure = new FillEmptyColumnStructureDto();
        fillEmptyColumnStructure.converterType(ConverterTypeDto.FILL_EMPTY_COLUMN)
                .columnIndex(reportDto.getColumnsToFill());
        log.debug("Finish buildFillEmptyColumnStructure");
        tableStructure.addStructuresItem(fillEmptyColumnStructure);
    }

    /**
     * Builds converter structure to fill partially filled rows.
     */
    private void buildRemoveKeywordStructure(RemoveKeywordsSettingsDto removeKeywordsSettingsDto) {
        log.debug("Start buildRemoveKeywordStructure");
        RemoveKeywordsStructureDto removeKeywordsStructure = new RemoveKeywordsStructureDto();
        removeKeywordsStructure.converterType(ConverterTypeDto.REMOVE_KEYWORD)
                .keywords(removeKeywordsSettingsDto.getKeywords())
                .removeRows(removeKeywordsSettingsDto.isRemoveRows())
                .removeColumns(removeKeywordsSettingsDto.isRemoveColumns())
                .ignoreCase(removeKeywordsSettingsDto.isIgnoreCase())
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.
                        valueOf(removeKeywordsSettingsDto.getMatchType().getValue()));
        log.debug("Finish buildRemoveKeywordStructure");
        tableStructure.addStructuresItem(removeKeywordsStructure);
    }

    private void buildRemoveRowByIndexStructure(List<Integer> rowIndex) {
        log.debug("Start buildRemoveRowByIndexStructureDto");
        RemoveRowByIndexStructureDto removeRowByIndexStructure = new RemoveRowByIndexStructureDto();
        removeRowByIndexStructure.converterType(ConverterTypeDto.REMOVE_ROW_BY_INDEX)
                .rowIndex(rowIndex);
        log.debug("Finish buildRemoveRowByIndexStructureDto");
        tableStructure.addStructuresItem(removeRowByIndexStructure);
    }

    private void buildRemoveColumnByIndexStructure(List<Integer> columnIndex) {
        log.debug("Start buildRemoveColumnByIndexStructureDto");
        RemoveColumnByIndexStructureDto removeColumnByIndexStructure = new RemoveColumnByIndexStructureDto();
        removeColumnByIndexStructure.converterType(ConverterTypeDto.REMOVE_COLUMN_BY_INDEX)
                .columnIndex(columnIndex);
        log.debug("Finish buildRemoveColumnByIndexStructureDto");
        tableStructure.addStructuresItem(removeColumnByIndexStructure);
    }

}
