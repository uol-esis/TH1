package de.uol.pgdoener.th1.api.payload.validation;

import de.uol.pgdoener.th1.api.payload.request.CreateStructure;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidStructureValidator implements ConstraintValidator<ValidStructure, CreateStructure> {

    @Override
    public boolean isValid(CreateStructure structure, ConstraintValidatorContext context) {
        if (structure == null) {
            return true; // Falls null erlaubt ist
        }
        String converterType = structure.getConverterType();

        if ("FILL_EMPTY_CELLS".equals(converterType)) {
            return structure.getRowIndex() != null && !(structure.getRowIndex().length == 0);
        } else if ("REMOVE_COLUMN_BY_INDEX".equals(converterType)) {
            return structure.getColumnIndex() != null && !(structure.getColumnIndex().length == 0);
        } else if ("REMOVE_GROUPED_HEADER".equals(converterType)) {
            return structure.getStartR() != null && structure.getEndR() != null
                    && structure.getStartC() != null && structure.getEndC() != null;
        }
        return false;
    }
}

/*
public class ValidStructureValidator implements ConstraintValidator<ValidStructure, StructureDto> {

    @Override
    public boolean isValid(StructureDto structure, ConstraintValidatorContext context) {
        System.out.println("dskdapkdaskdkasdölkasöld");

        if (structure == null) {
            return true; // Falls null erlaubt ist
        }

        String converterType = structure.converterType().toString();



        if ("FILL_EMPTY_CELLS".equals(converterType)) {
            return structure.rows().isPresent();
        } else if ("REMOVE_COLUMN_BY_INDEX".equals(converterType)) {
            return structure.columns().isPresent();
        } else if ("REMOVE_GROUPED_HEADER".equals(converterType)) {
            return structure.startRow().isPresent() && structure.endRow().isPresent()
                    && structure.startColumn().isPresent() && structure.endColumn().isPresent();
        }
        return false; // Ungültig, wenn keine der Bedingungen erfüllt ist
    }
}*/
