package system.payments.poc.factory.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.repository.RefundTransactionRepository;
import system.payments.poc.service.MerchantService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static system.payments.poc.factory.TransactionTestUtil.generateTransactionInputDto;

@ExtendWith(MockitoExtension.class)
class RefundTransactionFactoryTest {
    private TransactionFactory transactionFactory;

    @Mock
    private MerchantService merchantService;

    @Mock
    private RefundTransactionRepository transactionRepository;

    @Mock
    private ChargeTransactionRepository referenceTransactionRepository;

    @BeforeEach
    void setUp() {
        transactionFactory = new RefundTransactionFactory(merchantService, transactionRepository, referenceTransactionRepository);
    }


    @ParameterizedTest
    @CsvSource({"APPROVED, APPROVED, REFUNDED", "REVERSED, ERROR, REVERSED"})
    void createTransaction_success(String refStatus, String resultStatus, String resultRefStatus) {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();

        ChargeTransaction refTransaction = new ChargeTransaction();
        refTransaction.setStatus(TransactionStatus.valueOf(refStatus));
        refTransaction.setUuid(transactionInputDto.getReferenceId());
        refTransaction.setAmount(transactionInputDto.getAmount());

        when(transactionRepository.save(any(RefundTransaction.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceTransactionRepository.findById(transactionInputDto.getReferenceId())).thenReturn(Optional.of(refTransaction));
        when(merchantService.findById(transactionInputDto.getMerchantId())).thenReturn(merchant);

        Transaction transaction = transactionFactory.createTransaction(transactionInputDto);

        verify(transactionRepository).save(any(RefundTransaction.class));
        verify(referenceTransactionRepository).save(any(ChargeTransaction.class));
        assertEquals(transaction.getClass(), RefundTransaction.class);
        assertEquals(transaction.getCustomerEmail(), transactionInputDto.getCustomerEmail());
        assertEquals(transaction.getCustomerPhone(), transactionInputDto.getCustomerPhone());
        assertEquals(transaction.getMerchant(), merchant);
        assertEquals(transaction.getReferenceTransaction(), refTransaction);
        assertEquals(transaction.getAmount(), transactionInputDto.getAmount());
        assertEquals(transaction.getStatus(), TransactionStatus.valueOf(resultStatus));
        assertEquals(refTransaction.getStatus(), TransactionStatus.valueOf(resultRefStatus));

        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            verify(merchantService).updateTotalTransactionSum(merchant, transactionInputDto.getAmount().negate());
        }
    }

    @Test
    void createTransaction_failure_refNotFound() {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        when(referenceTransactionRepository.findById(transactionInputDto.getReferenceId())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionFactory.createTransaction(transactionInputDto));
    }
}