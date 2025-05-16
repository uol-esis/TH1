package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.TableStructureGenerationSettingsDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableStructureSettingsMapper {

    public TableStructureGenerationSettingsDto unwrap(Optional<TableStructureGenerationSettingsDto> optioanlSettings) {
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        if (settings.getRemoveHeader().isPresent()) {
            
        }

    }
}
