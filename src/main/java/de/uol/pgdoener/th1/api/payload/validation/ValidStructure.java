package de.uol.pgdoener.th1.api.payload.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidStructureValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStructure {
    String message() default "Invalid structure configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
