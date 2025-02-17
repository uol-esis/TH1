package de.uol.pgdoener.th1.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = ValidStructureValidator.class)
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStructure {

    String message() default "Invalid structure configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
