package de.uol.pgdoener.th1.api.payload.validation;

import de.uol.pgdoener.th1.business.enums.ConverterType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsConverterTypeValidator implements ConstraintValidator<IsConverterType, String>
{
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {return false;}
        try {
            ConverterType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
