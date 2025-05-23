package de.uol.pgdoener.th1.api.validation;

import de.uol.pgdoener.th1.business.dto.ReportTypeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidReportTypeValidator implements ConstraintValidator<ValidReportType, ReportTypeDto> {

    @Override
    public boolean isValid(ReportTypeDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value != ReportTypeDto.UNKNOWN_DEFAULT_OPEN_API;
    }

}
