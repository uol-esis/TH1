package de.uol.pgdoener.th1.api.validation;

import de.uol.pgdoener.th1.business.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.business.dto.StructureDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidStructureValidator implements ConstraintValidator<ValidStructure, StructureDto> {

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public boolean isValid(StructureDto structure, ConstraintValidatorContext context) {
        if (structure == null) {
            return false;
        }
        ConverterTypeDto converterType = structure.getConverterType();

        return switch (converterType) {
            case REMOVE_GROUPED_HEADER -> structure.getRowIndex() != null
                    && !structure.getRowIndex().isEmpty()
                    && structure.getColumnIndex() != null
                    && !structure.getColumnIndex().isEmpty();
            case FILL_EMPTY_ROW -> structure.getRowIndex() != null && !structure.getRowIndex().isEmpty();
            case REMOVE_COLUMN_BY_INDEX -> structure.getColumnIndex() != null && !structure.getColumnIndex().isEmpty();
            case REMOVE_ROW_BY_INDEX -> structure.getRowIndex() != null && !structure.getRowIndex().isEmpty();
            case ADD_HEADER_NAME -> structure.getHeaderNames() != null && !structure.getHeaderNames().isEmpty();
            default -> false;
        };
    }

}
