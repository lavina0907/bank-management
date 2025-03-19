package com.cgi.bank_management.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
  Class<? extends Enum<?>> enumClass();

  String message() default "Invalid value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
