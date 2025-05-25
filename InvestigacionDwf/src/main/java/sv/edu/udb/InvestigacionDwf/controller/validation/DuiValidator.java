package sv.edu.udb.InvestigacionDwf.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuiValidator implements ConstraintValidator<Dui, String> {

    private Pattern pattern;

    @Override
    public void initialize(Dui constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
