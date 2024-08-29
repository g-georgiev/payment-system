package system.payments.poc.factory.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.exceptions.TransactionNotFoundException;
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
    @Mock
    private AuthorizeTransactionRepository authorizeTransactionRepository;

    @BeforeEach
    void setUp() {
        transactionFactory = new RefundTransactionFactory(merchantService,
                transactionRepository,
                referenceTransactionRepository,
                authorizeTransactionRepository);
    }


    @ParameterizedTest
    @CsvSource({"APPROVED, APPROVED, REFUNDED", "REVERSED, ERROR, REVERSED"})
    void createTransaction_success(String refStatus, String resultStatus, String resultRefStatus) {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.ACTIVE);

        ChargeTransaction refTransaction = new ChargeTransaction();
        refTransaction.setStatus(TransactionStatus.valueOf(refStatus));
        refTransaction.setUuid(transactionInputDto.getReferenceId());
        refTransaction.setAmount(transactionInputDto.getAmount());
        refTransaction.setMerchant(merchant);
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        refTransaction.setReferenceTransaction(authorizeTransaction);

        when(transactionRepository.save(any(RefundTransaction.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceTransactionRepository.findByUuid(transactionInputDto.getReferenceId())).thenReturn(Optional.of(refTransaction));

        Transaction transaction = transactionFactory.createTransaction(transactionInputDto);

        verify(transactionRepository).save(any(RefundTransaction.class));
        verify(referenceTransactionRepository).save(any(ChargeTransaction.class));
        assertEquals(RefundTransaction.class, transaction.getClass());
        assertEquals(merchant, transaction.getMerchant());
        assertEquals(refTransaction, transaction.getReferenceTransaction());
        assertEquals(transactionInputDto.getAmount(), transaction.getAmount());
        assertEquals(TransactionStatus.valueOf(resultStatus), transaction.getStatus());
        assertEquals(TransactionStatus.valueOf(resultRefStatus), refTransaction.getStatus());

        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            assertEquals(TransactionStatus.valueOf(resultRefStatus), refTransaction.getReferenceTransaction().getStatus());
            verify(merchantService).updateTotalTransactionSum(merchant, transactionInputDto.getAmount().negate());
        }
    }

    @Test
    void createTransaction_failure_refNotFound() {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.ACTIVE);
        ChargeTransaction refTransaction = new ChargeTransaction();
        refTransaction.setMerchant(merchant);
        when(referenceTransactionRepository.findByUuid(transactionInputDto.getReferenceId())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionFactory.createTransaction(transactionInputDto));
    }
}