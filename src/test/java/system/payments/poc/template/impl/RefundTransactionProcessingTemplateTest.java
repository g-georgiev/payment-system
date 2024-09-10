package system.payments.poc.template.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.repository.RefundTransactionRepository;
import system.payments.poc.service.MerchantService;
import system.payments.poc.service.UserCredentialsService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefundTransactionProcessingTemplateTest {

    @Mock
    private MerchantService merchantService;

    @Mock
    private UserCredentialsService userCredentialsService;

    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private RefundTransactionRepository transactionRepository;

    @Mock
    private ChargeTransactionRepository referenceTransactionRepository;

    @Mock
    private AuthorizeTransactionRepository authorizeTransactionRepository;

    @InjectMocks
    private RefundTransactionProcessingTemplate template;

    @ParameterizedTest
    @CsvSource({"APPROVED", "ERROR"})
    public void test_process_refund_transaction_successfully_with_reference(String status) {
        // Arrange
        UUID referenceId = UUID.randomUUID();
        Merchant merchant = new Merchant();
        RefundTransaction refundTransaction = new RefundTransaction();
        refundTransaction.setStatus(TransactionStatus.valueOf(status));
        refundTransaction.setMerchant(merchant);
        ChargeTransaction chargeTransaction = new ChargeTransaction();
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        chargeTransaction.setReferenceTransaction(authorizeTransaction);
        refundTransaction.setReferenceTransaction(chargeTransaction);

        TransactionInputDto transactionInputDto = TransactionInputDto.builder()
                .referenceId(referenceId)
                .build();

        when(referenceTransactionRepository.findByUuid(referenceId)).thenReturn(chargeTransaction);
        when(transactionFactory.createTransaction(transactionInputDto, null, chargeTransaction)).thenReturn(refundTransaction);

        // Act
        Transaction result = template.process(transactionInputDto);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).save((RefundTransaction) result);
        if (result.getStatus().equals(TransactionStatus.APPROVED)) {
            assertEquals(TransactionStatus.REFUNDED, result.getReferenceTransaction().getStatus());
            assertEquals(TransactionStatus.REFUNDED, result.getReferenceTransaction().getReferenceTransaction().getStatus());
            verify(merchantService).updateTotalTransactionSum(merchant, refundTransaction.getAmount().negate());
            verify(referenceTransactionRepository).save(chargeTransaction);
            verify(authorizeTransactionRepository).save(authorizeTransaction);
        } else {
            verify(merchantService, times(0)).updateTotalTransactionSum(merchant, refundTransaction.getAmount().negate());
            verify(referenceTransactionRepository, times(0)).save(chargeTransaction);
            verify(authorizeTransactionRepository, times(0)).save(authorizeTransaction);
        }
    }
}