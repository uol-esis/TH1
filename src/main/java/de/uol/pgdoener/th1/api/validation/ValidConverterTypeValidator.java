package de.uol.pgdoener.th1.api.validation;

import de.uol.pgdoener.th1.application.dto.ConverterTypeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidConverterTypeValidator implements ConstraintValidator<ValidConverterType, ConverterTypeDto> {

    @Override
    public boolean isValid(ConverterTypeDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value != ConverterTypeDto.UNKNOWN_DEFAULT_OPEN_API;
    }

}
