package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class TableStructureBuilder {

    private final TableStructureDto tableStructure;

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
    private void buildFillEmptyRowStructure(List<Integer> rowsToFill) {
        log.debug("Start buildFillEmptyRowStructure");
        FillEmptyRowStructureDto fillEmptyRowStructure = new FillEmptyRowStructureDto();
        fillEmptyRowStructure.converterType(ConverterTypeDto.FILL_EMPTY_ROW)
                .rowIndex(rowsToFill);
        log.debug("Finish buildFillEmptyRowStructure");
        tableStructure.addStructuresItem(fillEmptyRowStructure);
    }

}
