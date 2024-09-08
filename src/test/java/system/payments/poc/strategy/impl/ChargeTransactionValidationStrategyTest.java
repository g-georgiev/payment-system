package system.payments.poc.strategy.impl;

import org.junit.jupiter.api.Test;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.repository.AuthorizeTransactionRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChargeTransactionValidationStrategyTest {

    // Validate transaction with valid referenceId
    @Test
    public void test_validate_transaction_with_valid_referenceId() {
        UUID validReferenceId = UUID.randomUUID();
        TransactionInputDto transaction = TransactionInputDto.builder()
                .referenceId(validReferenceId)
                .build();

        AuthorizeTransactionRepository mockRepository = mock(AuthorizeTransactionRepository.class);
        when(mockRepository.existsByUuid(validReferenceId)).thenReturn(true);

        ChargeTransactionValidationStrategy strategy = new ChargeTransactionValidationStrategy(mockRepository);
        String result = strategy.validateTransaction(transaction);

        assertNull(result);
    }

    // Validate transaction with null referenceId
    @Test
    public void test_validate_transaction_with_null_referenceId() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .referenceId(null)
                .build();

        AuthorizeTransactionRepository mockRepository = mock(AuthorizeTransactionRepository.class);

        ChargeTransactionValidationStrategy strategy = new ChargeTransactionValidationStrategy(mockRepository);
        String result = strategy.validateTransaction(transaction);

        assertEquals("Reference id is mandatory for CHARGE transactions", result);
    }

    // Validate transaction with non-existing referenceId in repository
    @Test
    public void test_validate_transaction_with_non_existing_referenceId() {
        UUID nonExistingReferenceId = UUID.randomUUID();
        TransactionInputDto transaction = TransactionInputDto.builder()
                .referenceId(nonExistingReferenceId)
                .build();

        AuthorizeTransactionRepository mockRepository = mock(AuthorizeTransactionRepository.class);
        when(mockRepository.existsByUuid(nonExistingReferenceId)).thenReturn(false);

        ChargeTransactionValidationStrategy strategy = new ChargeTransactionValidationStrategy(mockRepository);
        String result = strategy.validateTransaction(transaction);

        assertEquals("AUTHORIZE transaction reference id " + nonExistingReferenceId + " does not exist", result);
    }
}