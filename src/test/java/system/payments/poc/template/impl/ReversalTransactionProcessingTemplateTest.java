package system.payments.poc.template.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ReversalTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ReversalTransactionRepository;
import system.payments.poc.service.UserCredentialsService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReversalTransactionProcessingTemplateTest {

    // Process a valid reversal transaction successfully
    @ParameterizedTest
    @CsvSource({"APPROVED", "ERROR"})
    public void test_process_valid_reversal_transaction_successfully(String status) {
        // Arrange
        UserCredentialsService userCredentialsService = mock(UserCredentialsService.class);
        TransactionFactory transactionFactory = mock(TransactionFactory.class);
        ReversalTransactionRepository reversalTransactionRepository = mock(ReversalTransactionRepository.class);
        AuthorizeTransactionRepository authorizeTransactionRepository = mock(AuthorizeTransactionRepository.class);
        ReversalTransactionProcessingTemplate template = new ReversalTransactionProcessingTemplate(
                userCredentialsService, transactionFactory, reversalTransactionRepository, authorizeTransactionRepository
        );

        UUID referenceId = UUID.randomUUID();
        AuthorizeTransaction referenceTransaction = new AuthorizeTransaction();
        referenceTransaction.setUuid(referenceId);

        when(authorizeTransactionRepository.findByUuid(referenceId)).thenReturn(referenceTransaction);

        TransactionInputDto inputDto = TransactionInputDto.builder().referenceId(referenceId).build();

        ReversalTransaction reversalTransaction = new ReversalTransaction();
        reversalTransaction.setReferenceTransaction(referenceTransaction);
        reversalTransaction.setStatus(TransactionStatus.valueOf(status));

        when(transactionFactory.createTransaction(inputDto, null, referenceTransaction)).thenReturn(reversalTransaction);

        // Act
        Transaction result = template.process(inputDto);

        // Assert
        assertNotNull(result);
        verify(reversalTransactionRepository).save(reversalTransaction);
        if (result.getStatus().equals(TransactionStatus.APPROVED)) {
            assertEquals(TransactionStatus.REVERSED, referenceTransaction.getStatus());
            verify(authorizeTransactionRepository).save(referenceTransaction);
        } else {
            verify(authorizeTransactionRepository, times(0)).save(referenceTransaction);
        }
    }
}