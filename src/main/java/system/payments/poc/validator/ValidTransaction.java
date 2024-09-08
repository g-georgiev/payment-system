package system.payments.poc.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import system.payments.poc.validator.impl.TransactionValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransactionValidator.class)
@Documented
public @interface ValidTransaction {
    String message() default "Transaction is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
