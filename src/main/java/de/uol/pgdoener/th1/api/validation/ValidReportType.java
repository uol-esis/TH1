package de.uol.pgdoener.th1.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = ValidReportTypeValidator.class)
public @interface ValidReportType {

    String message() default "invalid reportType";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
