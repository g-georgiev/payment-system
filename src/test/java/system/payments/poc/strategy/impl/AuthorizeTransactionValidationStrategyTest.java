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
import system.payments.poc.service.MerchantService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizeTransactionValidationStrategyTest {

    @Mock
    private MerchantService merchantService;

    @InjectMocks
    private AuthorizeTransactionValidationStrategy strategy;

    // Transaction with missing merchant id returns false
    @Test
    public void transaction_with_missing_merchant_id_returns_false() {
        // Arrange
        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .merchantId(null)
                .build();

        // Act
        String result = strategy.validateTransaction(transactionInputDto);

        // Assert
        assertEquals("MerchantId cannot be null for AUTHORIZE Transactions", result);
    }

    // Transaction with missing merchant returns false
    @Test
    public void transaction_with_missing_merchant_returns_false() {
        // Arrange
        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .merchantId(1L)
                .build();

        when(merchantService.findById(1L)).thenReturn(null);

        // Act
        String result = strategy.validateTransaction(transactionInputDto);

        // Assert
        assertEquals("Merchant with id 1 not found", result);
    }

    // Transaction with inactive merchant returns false
    @Test
    public void transaction_with_inactive_merchant_returns_false() {
        // Arrange
        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .merchantId(1L)
                .build();

        Merchant inactiveMerchant = new Merchant();
        inactiveMerchant.setId(1L);
        inactiveMerchant.setStatus(MerchantStatus.INACTIVE);
        when(merchantService.findById(1L)).thenReturn(inactiveMerchant);

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
                .merchantId(1L)
                .referenceId(null)
                .build();

        Merchant inactiveMerchant = new Merchant();
        inactiveMerchant.setId(1L);
        inactiveMerchant.setStatus(MerchantStatus.ACTIVE);
        when(merchantService.findById(1L)).thenReturn(inactiveMerchant);

        String result = strategy.validateTransaction(transaction);
        assertNull(result);
    }

    // Validate transaction with null amount returns appropriate error message
    @Test
    public void validate_transaction_with_null_amount_returns_appropriate_error_message() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(null)
                .merchantId(1L)
                .referenceId(null)
                .build();

        Merchant inactiveMerchant = new Merchant();
        inactiveMerchant.setId(1L);
        inactiveMerchant.setStatus(MerchantStatus.ACTIVE);
        when(merchantService.findById(1L)).thenReturn(inactiveMerchant);

        String result = strategy.validateTransaction(transaction);
        assertEquals("Amount cannot be null for AUTHORIZE Transactions", result);
    }

    // Validate transaction with amount as zero
    @Test
    public void test_validate_transaction_with_amount_as_zero() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(BigDecimal.ZERO)
                .merchantId(1L)
                .referenceId(null)
                .build();

        Merchant inactiveMerchant = new Merchant();
        inactiveMerchant.setId(1L);
        inactiveMerchant.setStatus(MerchantStatus.ACTIVE);
        when(merchantService.findById(1L)).thenReturn(inactiveMerchant);

        String result = strategy.validateTransaction(transaction);
        assertEquals("Amount must be positive for AUTHORIZE Transactions", result);
    }

    // Validate transaction with non-null referenceId returns appropriate error message
    @Test
    public void validate_transaction_with_non_null_referenceId_returns_appropriate_error_message() {
        TransactionInputDto transaction = TransactionInputDto.builder()
                .amount(new BigDecimal("100.00"))
                .merchantId(1L)
                .referenceId(UUID.randomUUID())
                .build();

        Merchant inactiveMerchant = new Merchant();
        inactiveMerchant.setId(1L);
        inactiveMerchant.setStatus(MerchantStatus.ACTIVE);
        when(merchantService.findById(1L)).thenReturn(inactiveMerchant);

        String result = strategy.validateTransaction(transaction);
        assertEquals("ReferenceId should not be provided for AUTHORIZE Transactions", result);
    }
}