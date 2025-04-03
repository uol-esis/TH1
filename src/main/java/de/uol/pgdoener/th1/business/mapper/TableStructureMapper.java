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
import java.util.Optional;

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

        TableStructureDto dto = new TableStructureDto();
        dto.setName(tableStructure.getName());
        dto.setStructures(structureDtoList);
        dto.setId(Optional.ofNullable(tableStructure.getId()));
        dto.setEndRow(Optional.ofNullable(tableStructure.getEndRow()));
        dto.setEndColumn(Optional.ofNullable(tableStructure.getEndColumn()));

        return dto;
    }

    public static TableStructure toEntity(TableStructureDto tableStructureDto) {
        return new TableStructure(
                null,
                tableStructureDto.getName(),
                tableStructureDto.getEndRow().orElse(null),
                tableStructureDto.getEndColumn().orElse(null)
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
