package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.ColumnTypeMismatchReport;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.GroupedHeaderReport;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.Report;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TableStructureBuilder {

    @Getter
    private final TableStructureDto tableStructure = defaultTableStructure();

    private TableStructureDto defaultTableStructure() {
        TableStructureDto tableStructure = new TableStructureDto();
        tableStructure.setName("");
        buildRemoveHeaderStructure();
        buildRemoveFooterStructure();
        buildRemoveTrailingColumnStructure();
        return tableStructure;
    }

    /**
     * Constructs the full table structure with necessary converters applied.
     */
    public TableStructureDto buildTableStructure(List<Report> reports) {
        List<Report> unresolvedReports = new ArrayList<>();
        for (Report report : reports) {
            switch (report) {
                case GroupedHeaderReport r -> buildGroupHeaderStructure(r);
                case ColumnTypeMismatchReport r -> unresolvedReports.add(r);
            }
        }

        return tableStructure;
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
    private void buildGroupHeaderStructure(
            List<Integer> colIndex, List<Integer> rowIndex, Integer startRow
    ) {
        log.debug("Start buildGroupHeaderStructure");
        RemoveGroupedHeaderStructureDto groupHeaderStructure = new RemoveGroupedHeaderStructureDto();
        groupHeaderStructure.setConverterType(ConverterTypeDto.REMOVE_GROUPED_HEADER);
        groupHeaderStructure.setColumnIndex(colIndex);
        groupHeaderStructure.setRowIndex(rowIndex);
        groupHeaderStructure.setStartRow(Optional.of(startRow));
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
