package system.payments.poc.template.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.service.MerchantService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorizeTransactionProcessingTemplateTest {

    // Successfully process a transaction with valid input data
    @Test
    public void test_process_transaction_with_valid_input() {
        // Arrange
        MerchantService merchantService = Mockito.mock(MerchantService.class);
        TransactionFactory transactionFactory = mock(TransactionFactory.class);
        AuthorizeTransactionRepository transactionRepository = mock(AuthorizeTransactionRepository.class);
        AuthorizeTransactionProcessingTemplate template = new AuthorizeTransactionProcessingTemplate(merchantService, transactionFactory, transactionRepository);

        TransactionInputDto inputDto = TransactionInputDto.builder()
                .merchantId(1L)
                .customerEmail("test@example.com")
                .customerPhone("1234567890")
                .build();

        Merchant merchant = new Merchant();
        when(merchantService.findById(1L)).thenReturn(merchant);

        AuthorizeTransaction transaction = new AuthorizeTransaction();
        when(transactionFactory.createTransaction(inputDto, merchant, null)).thenReturn(transaction);

        // Act
        Transaction result = template.process(inputDto);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).save(transaction);
    }
}