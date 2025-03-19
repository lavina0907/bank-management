package com.cgi.bank_management.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

  private Class<? extends Enum<?>> enumClass;

  @Override
  public void initialize(EnumValue constraintAnnotation) {
    this.enumClass = constraintAnnotation.enumClass();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (value == null) {
      return false;
    }
    for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
      if (enumConstant.name().equals(value)) {
        return true; // valid if the value matches one of the enum constants
      }
    }
    return false;
  }
}
