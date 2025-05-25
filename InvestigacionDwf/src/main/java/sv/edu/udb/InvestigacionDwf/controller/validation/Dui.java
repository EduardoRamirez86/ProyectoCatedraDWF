package sv.edu.udb.InvestigacionDwf.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DuiValidator.class)
@Documented
public @interface Dui {

    String message() default "Formato de DUI inválido. Debe ser como: 12345678-9";

    String pattern() default "^\\d{8}-\\d$";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
