package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.api.payload.request.CreateStructure;
import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.StructureSummaryDto;
import de.uol.pgdoener.th1.business.enums.ConverterType;
import de.uol.pgdoener.th1.data.entity.Structure;

import java.util.Optional;

public abstract class StructureMapper {

    static public StructureDto toDto(CreateStructure structureRequest) {
        ConverterType converterType = ConverterType.valueOf(structureRequest.getConverterType());
        return new StructureDto(
                converterType,
                Optional.ofNullable(structureRequest.getColumnIndex()),
                Optional.ofNullable(structureRequest.getRowIndex()),
                Optional.ofNullable(structureRequest.getStartR()),
                Optional.ofNullable(structureRequest.getEndR()),
                Optional.ofNullable(structureRequest.getStartC()),
                Optional.ofNullable(structureRequest.getEndC())
        );
    }

    public static StructureDto toDto(Structure entity) {
        return new StructureDto(
                entity.getConverterType(),
                Optional.ofNullable(entity.getColumns()),
                Optional.ofNullable(entity.getRows()),
                Optional.ofNullable(entity.getStartRow()),
                Optional.ofNullable(entity.getEndRow()),
                Optional.ofNullable(entity.getStartColumn()),
                Optional.ofNullable(entity.getEndColumn())
        );
    }

    public static Structure toEntity(StructureDto dto, int position, Long tableStructureId) {
        return new Structure(
                null, // ID wird von der Datenbank generiert
                dto.converterType(),
                dto.columns().orElse(null),
                dto.rows().orElse(null),
                dto.startRow().orElse(null),
                dto.endRow().orElse(null),
                dto.startColumn().orElse(null),
                dto.endColumn().orElse(null),
                position,
                tableStructureId
        );
    }

    public static StructureSummaryDto toSummaryDto(Structure entity) {
        return new StructureSummaryDto(
                entity.getConverterType()
        );
    }
}
