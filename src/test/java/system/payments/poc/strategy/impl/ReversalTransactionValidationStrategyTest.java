package system.payments.poc.strategy.impl;

import org.junit.jupiter.api.Test;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.security.UserSecurity;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.service.UserCredentialsService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReversalTransactionValidationStrategyTest {

    // Validate transaction with valid referenceId
    @Test
    public void test_validate_transaction_with_valid_transaction() {

        UUID referenceId = UUID.randomUUID();
        AuthorizeTransaction referenceTransaction = new AuthorizeTransaction();
        referenceTransaction.setUuid(referenceId);
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        referenceTransaction.setMerchant(merchant);

        AuthorizeTransactionRepository referenceTransactionRepository = mock(AuthorizeTransactionRepository.class);

        when(referenceTransactionRepository.findByUuid(referenceId)).thenReturn(referenceTransaction);

        UserCredentialsService userCredentialsService = mock(UserCredentialsService.class);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(merchant);

        ReversalTransactionValidationStrategy strategy = new ReversalTransactionValidationStrategy(referenceTransactionRepository, userCredentialsService);

        TransactionInputDto transaction = TransactionInputDto.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(referenceId)
                .build();

        String result = strategy.validateTransaction(transaction);

        assertNull(result);
    }

    @Test
    public void test_validate_transaction_with_different_merchant() {

        UUID referenceId = UUID.randomUUID();
        AuthorizeTransaction referenceTransaction = new AuthorizeTransaction();
        referenceTransaction.setUuid(referenceId);
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        referenceTransaction.setMerchant(merchant);

        AuthorizeTransactionRepository referenceTransactionRepository = mock(AuthorizeTransactionRepository.class);

        when(referenceTransactionRepository.findByUuid(referenceId)).thenReturn(referenceTransaction);

        UserCredentialsService userCredentialsService = mock(UserCredentialsService.class);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        Merchant merchant2 = new Merchant();
        merchant.setId(2L);
        when(userSecurity.getUserCredentials()).thenReturn(merchant2);

        ReversalTransactionValidationStrategy strategy = new ReversalTransactionValidationStrategy(referenceTransactionRepository, userCredentialsService);

        TransactionInputDto transaction = TransactionInputDto.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(referenceId)
                .build();

        String result = strategy.validateTransaction(transaction);

        assertEquals("AUTHORIZE transaction reference MUST belong to current merchant", result);
    }

    // Validate transaction with null referenceId
    @Test
    public void test_validate_transaction_with_null_referenceId() {
        AuthorizeTransactionRepository referenceTransactionRepository = mock(AuthorizeTransactionRepository.class);
        UserCredentialsService userCredentialsService = mock(UserCredentialsService.class);

        ReversalTransactionValidationStrategy strategy = new ReversalTransactionValidationStrategy(referenceTransactionRepository, userCredentialsService);

        TransactionInputDto transaction = TransactionInputDto.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(null)
                .build();

        String result = strategy.validateTransaction(transaction);

        assertEquals("Reference id is mandatory for REVERSAL transactions", result);
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
        UserCredentialsService userCredentialsService = mock(UserCredentialsService.class);

        ReversalTransactionValidationStrategy strategy = new ReversalTransactionValidationStrategy(mockRepository, userCredentialsService);
        String result = strategy.validateTransaction(transaction);

        assertEquals("AUTHORIZE transaction reference id " + nonExistingReferenceId + " does not exist", result);
    }
}