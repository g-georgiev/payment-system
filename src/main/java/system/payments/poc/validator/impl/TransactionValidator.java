package system.payments.poc.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.strategy.TransactionValidationStrategy;
import system.payments.poc.validator.ValidTransaction;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TransactionValidator implements ConstraintValidator<ValidTransaction, TransactionInputDto> {
    private final Map<TransactionType, TransactionValidationStrategy> validationStrategyMap;

    @Override
    public void initialize(ValidTransaction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TransactionInputDto transactionInputDto, ConstraintValidatorContext constraintValidatorContext) {
        String errorMessage = validationStrategyMap.get(transactionInputDto.getTransactionType()).validateTransaction(transactionInputDto);

        if (Objects.nonNull(errorMessage)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
            return false;
        }

        return true;
    }
}
