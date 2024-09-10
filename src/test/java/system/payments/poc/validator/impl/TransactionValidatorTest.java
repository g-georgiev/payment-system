package system.payments.poc.validator.impl;


import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.model.Merchant;
import system.payments.poc.strategy.TransactionValidationStrategy;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionValidatorTest {

    // Valid transaction with active merchant returns true
    @Test
    public void valid_transaction_with_active_merchant_returns_true() {
        // Arrange
        Map<TransactionType, TransactionValidationStrategy> validationStrategyMap = mock(Map.class);
        TransactionValidationStrategy validationStrategy = mock(TransactionValidationStrategy.class);
        TransactionValidator validator = new TransactionValidator(validationStrategyMap);

        Merchant activeMerchant = new Merchant();
        activeMerchant.setId(1L);
        activeMerchant.setStatus(MerchantStatus.ACTIVE);

        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .build();

        when(validationStrategyMap.get(TransactionType.AUTHORIZE)).thenReturn(validationStrategy);
        when(validationStrategy.validateTransaction(transactionInputDto)).thenReturn(null);

        // Act
        boolean result = validator.isValid(transactionInputDto, mock(ConstraintValidatorContext.class));

        // Assert
        assertTrue(result);
    }

    // Transaction with validation error from strategy
    @Test
    public void transaction_with_validation_error_from_strategy() {
        // Arrange
        Map<TransactionType, TransactionValidationStrategy> validationStrategyMap = mock(Map.class);
        TransactionValidationStrategy validationStrategy = mock(TransactionValidationStrategy.class);
        TransactionValidator validator = new TransactionValidator(validationStrategyMap);

        Merchant activeMerchant = new Merchant();
        activeMerchant.setId(1L);
        activeMerchant.setStatus(MerchantStatus.ACTIVE);

        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .build();

        when(validationStrategyMap.get(TransactionType.AUTHORIZE)).thenReturn(validationStrategy);
        when(validationStrategy.validateTransaction(transactionInputDto)).thenReturn("test error");

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate("test error")).thenReturn(builder);

        // Act
        boolean result = validator.isValid(transactionInputDto, context);

        // Assert
        assertFalse(result);
    }
}