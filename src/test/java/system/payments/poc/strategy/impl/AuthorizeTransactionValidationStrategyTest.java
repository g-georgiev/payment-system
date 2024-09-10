package system.payments.poc.strategy.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.security.UserSecurity;
import system.payments.poc.service.UserCredentialsService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizeTransactionValidationStrategyTest {

    @Mock
    private UserCredentialsService userCredentialsService;

    @InjectMocks
    private AuthorizeTransactionValidationStrategy strategy;

    // Transaction with inactive merchant returns false
    @Test
    public void transaction_with_inactive_merchant_returns_false() {
        // Arrange
        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .build();

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setStatus(MerchantStatus.INACTIVE);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(merchant);

        // Act
        String result = strategy.validateTransaction(transactionInputDto);

        // Assert
        assertEquals("Merchant with id 1 is not active", result);
    }


    // Validate transaction with valid amount and no referenceId returns null
    @Test
    public void test_validate_transaction_with_valid_amount_and_referenceId_returns_null() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(new BigDecimal("100.00"))
                .referenceId(null)
                .build();

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setStatus(MerchantStatus.ACTIVE);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(merchant);

        String result = strategy.validateTransaction(transaction);
        assertNull(result);
    }

    // Validate transaction with null amount returns appropriate error message
    @Test
    public void validate_transaction_with_null_amount_returns_appropriate_error_message() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(null)
                .referenceId(null)
                .build();

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setStatus(MerchantStatus.ACTIVE);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(merchant);

        String result = strategy.validateTransaction(transaction);
        assertEquals("Amount cannot be null for AUTHORIZE Transactions", result);
    }

    // Validate transaction with amount as zero
    @Test
    public void test_validate_transaction_with_amount_as_zero() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(BigDecimal.ZERO)
                .referenceId(null)
                .build();

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setStatus(MerchantStatus.ACTIVE);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(merchant);

        String result = strategy.validateTransaction(transaction);
        assertEquals("Amount must be positive for AUTHORIZE Transactions", result);
    }

    // Validate transaction with non-null referenceId returns appropriate error message
    @Test
    public void validate_transaction_with_non_null_referenceId_returns_appropriate_error_message() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(new BigDecimal("100.00"))
                .referenceId(UUID.randomUUID())
                .build();

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setStatus(MerchantStatus.ACTIVE);
        UserSecurity userSecurity = mock(UserSecurity.class);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(merchant);

        String result = strategy.validateTransaction(transaction);
        assertEquals("ReferenceId should not be provided for AUTHORIZE Transactions", result);
    }
}