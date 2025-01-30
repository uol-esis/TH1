package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.StructureSummaryDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class TableStructureMapper {

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
                tableStructure.getName(),
                String.valueOf(tableStructure.getDelimiter()),
                structureDtoList,
                tableStructure.getEndRow(),
                tableStructure.getEndColumn()
        ).id(tableStructure.getId());
    }

    public static TableStructure toEntity(TableStructureDto tableStructureDto) {
        return new TableStructure(
                null,
                tableStructureDto.getName(),
                tableStructureDto.getDelimiter().charAt(0),
                tableStructureDto.getEndRow(),
                tableStructureDto.getEndColumn()
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
