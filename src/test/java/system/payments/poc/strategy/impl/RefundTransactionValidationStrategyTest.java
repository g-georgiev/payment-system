package system.payments.poc.strategy.impl;

import org.junit.jupiter.api.Test;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.repository.ChargeTransactionRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RefundTransactionValidationStrategyTest {

    // Validate transaction with valid referenceId
    @Test
    public void test_validate_transaction_with_valid_referenceId() {
        UUID validReferenceId = UUID.randomUUID();
        TransactionInputDto transaction = TransactionInputDto.builder()
                .referenceId(validReferenceId)
                .build();

        ChargeTransactionRepository mockRepository = mock(ChargeTransactionRepository.class);
        when(mockRepository.existsByUuid(validReferenceId)).thenReturn(true);

        RefundTransactionValidationStrategy strategy = new RefundTransactionValidationStrategy(mockRepository);
        String result = strategy.validateTransaction(transaction);

        assertNull(result);
    }

    // Validate transaction with null referenceId
    @Test
    public void test_validate_transaction_with_null_referenceId() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .referenceId(null)
                .build();

        ChargeTransactionRepository mockRepository = mock(ChargeTransactionRepository.class);

        RefundTransactionValidationStrategy strategy = new RefundTransactionValidationStrategy(mockRepository);
        String result = strategy.validateTransaction(transaction);

        assertEquals("Reference id is mandatory for REFUND transactions", result);
    }

    // Validate transaction with non-existing referenceId in repository
    @Test
    public void test_validate_transaction_with_non_existing_referenceId() {
        UUID nonExistingReferenceId = UUID.randomUUID();
        TransactionInputDto transaction = TransactionInputDto.builder()
                .referenceId(nonExistingReferenceId)
                .build();

        ChargeTransactionRepository mockRepository = mock(ChargeTransactionRepository.class);
        when(mockRepository.existsByUuid(nonExistingReferenceId)).thenReturn(false);

        RefundTransactionValidationStrategy strategy = new RefundTransactionValidationStrategy(mockRepository);
        String result = strategy.validateTransaction(transaction);

        assertEquals("CHARGE transaction reference id " + nonExistingReferenceId + " does not exist", result);
    }
}