package system.payments.poc.template.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.service.MerchantService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChargeTransactionProcessingTemplateTest {

    // Processes a transaction successfully when all inputs are valid
    @ParameterizedTest
    @CsvSource({"APPROVED", "ERROR"})
    public void test_process_transaction_successfully_with_valid_inputs(String status) {
        // Arrange
        MerchantService merchantService = mock(MerchantService.class);
        TransactionFactory transactionFactory = mock(TransactionFactory.class);
        ChargeTransactionRepository transactionRepository = mock(ChargeTransactionRepository.class);
        AuthorizeTransactionRepository referenceTransactionRepository = mock(AuthorizeTransactionRepository.class);
        ChargeTransactionProcessingTemplate template = new ChargeTransactionProcessingTemplate(
                merchantService, transactionFactory, transactionRepository, referenceTransactionRepository
        );

        TransactionInputDto inputDto = TransactionInputDto.builder()
                .merchantId(1L)
                .referenceId(null)
                .customerEmail("test@example.com")
                .customerPhone("1234567890")
                .build();

        Merchant merchant = new Merchant();
        when(merchantService.findById(1L)).thenReturn(merchant);

        ChargeTransaction transaction = new ChargeTransaction();
        transaction.setStatus(TransactionStatus.valueOf(status));
        transaction.setMerchant(merchant);
        transaction.setAmount(BigDecimal.TEN);

        when(transactionFactory.createTransaction(inputDto, merchant, null)).thenReturn(transaction);

        // Act
        Transaction result = template.process(inputDto);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).save((ChargeTransaction) result);
        assertEquals(TransactionStatus.valueOf(status), result.getStatus());
        if (result.getStatus().equals(TransactionStatus.APPROVED)) {
            verify(merchantService).updateTotalTransactionSum(merchant, BigDecimal.TEN);
        } else {
            verify(merchantService, times(0)).updateTotalTransactionSum(merchant, BigDecimal.ZERO);
        }
    }
}