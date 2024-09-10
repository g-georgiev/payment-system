package system.payments.poc.template.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.service.MerchantService;
import system.payments.poc.service.UserCredentialsService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChargeTransactionProcessingTemplateTest {

    // Processes a transaction successfully when all inputs are valid
    @ParameterizedTest
    @CsvSource({"APPROVED", "ERROR"})
    public void test_process_transaction_successfully_with_valid_inputs(String status) {
        // Arrange
        MerchantService merchantService = mock(MerchantService.class);
        UserCredentialsService userCredentialsService = mock(UserCredentialsService.class);
        TransactionFactory transactionFactory = mock(TransactionFactory.class);
        ChargeTransactionRepository chargeTransactionRepository = mock(ChargeTransactionRepository.class);
        AuthorizeTransactionRepository authorizeTransactionRepository = mock(AuthorizeTransactionRepository.class);
        ChargeTransactionProcessingTemplate template = new ChargeTransactionProcessingTemplate(
                merchantService, transactionFactory, userCredentialsService, chargeTransactionRepository, authorizeTransactionRepository
        );

        UUID referenceId = UUID.randomUUID();
        AuthorizeTransaction referenceTransaction = new AuthorizeTransaction();
        referenceTransaction.setUuid(referenceId);
        referenceTransaction.setMerchant(new Merchant());
        referenceTransaction.setAmount(new BigDecimal("100.00"));

        when(authorizeTransactionRepository.findByUuid(referenceId)).thenReturn(referenceTransaction);

        TransactionInputDto inputDto = TransactionInputDto.builder().referenceId(referenceId).build();

        ChargeTransaction reversalTransaction = new ChargeTransaction();
        reversalTransaction.setReferenceTransaction(referenceTransaction);
        reversalTransaction.setStatus(TransactionStatus.valueOf(status));

        when(transactionFactory.createTransaction(inputDto, null, referenceTransaction)).thenReturn(reversalTransaction);

        // Act
        Transaction result = template.process(inputDto);

        // Assert
        assertNotNull(result);
        verify(chargeTransactionRepository).save(reversalTransaction);
        if (result.getStatus().equals(TransactionStatus.APPROVED)) {
            verify(merchantService).updateTotalTransactionSum(reversalTransaction.getMerchant(), reversalTransaction.getAmount());
        }
    }
}