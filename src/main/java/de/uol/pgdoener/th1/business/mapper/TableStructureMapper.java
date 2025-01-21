package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.api.payload.request.CreateStructure;
import de.uol.pgdoener.th1.api.payload.request.CreateTableStructure;
import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.StructureSummaryDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;

import java.util.ArrayList;
import java.util.List;

public abstract class TableStructureMapper {

    static public TableStructureDto toDto(CreateTableStructure createTableStructure) {
        ArrayList<StructureDto> structureDtoList = new ArrayList<>();
        for (CreateStructure structure : createTableStructure.getStructures()) {
            structureDtoList.add(StructureMapper.toDto(structure));
        }
        return new TableStructureDto(
                null,
                createTableStructure.getName(),
                createTableStructure.getDelimiter(),
                structureDtoList,
                createTableStructure.getEndRow(),
                createTableStructure.getEndColumn()
        );
    }

    public static TableStructureDto toDto(TableStructure tableStructure, List<Structure> structureList) {

        List<StructureDto> structureDtoList = new ArrayList<>(structureList.size());

        for (Structure structure : structureList) {
            StructureDto structureDto = StructureMapper.toDto(structure);
            while (structureDtoList.size() <= structure.getPosition()) {
                structureDtoList.add(null);
            }
            structureDtoList.set(structure.getPosition(), structureDto);
        }

        return new TableStructureDto(
                tableStructure.getId(),
                tableStructure.getName(),
                tableStructure.getDelimiter(),
                structureDtoList,
                tableStructure.getEndRow(),
                tableStructure.getEndColumn()
        );
    }

    public static TableStructure toEntity(TableStructureDto tableStructureDto) {
        return new TableStructure(
                null,
                tableStructureDto.name(),
                tableStructureDto.delimiter(),
                tableStructureDto.endRow(),
                tableStructureDto.endColumn()
        );
    }

    public static TableStructureSummaryDto toSummaryDto(TableStructure tableStructure, List<Structure> structureList) {

        List<StructureSummaryDto> structureDtoList = new ArrayList<>(structureList.size());

        for (Structure structure : structureList) {
            StructureSummaryDto structureDto = StructureMapper.toSummaryDto(structure);
            while (structureDtoList.size() <= structure.getPosition()) {
                structureDtoList.add(null);
            }
            structureDtoList.set(structure.getPosition(), structureDto);
        }

        return new TableStructureSummaryDto(
                tableStructure.getId(),
                tableStructure.getName(),
                structureDtoList
        );
    }
}
