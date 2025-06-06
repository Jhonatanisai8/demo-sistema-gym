package com.isai.gym.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) //aplicarse en clases o metodos
@Retention(RetentionPolicy.RUNTIME) // en tiempo de ejecucion
@Constraint(validatedBy = PasswordMatchesValidator.class) // el validador que lo implementa
@Documented
public @interface PasswordMatches {
    String message() default "Las contraseñas no coinciden";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
