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
import system.payments.poc.exceptions.MerchantInactiveException;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.ReversalTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ReversalTransactionRepository;
import system.payments.poc.service.MerchantService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static system.payments.poc.factory.TransactionTestUtil.generateTransactionInputDto;

@ExtendWith(MockitoExtension.class)
class ReversalTransactionFactoryTest {
    private TransactionFactory transactionFactory;

    @Mock
    private MerchantService merchantService;

    @Mock
    private ReversalTransactionRepository transactionRepository;

    @Mock
    private AuthorizeTransactionRepository referenceTransactionRepository;

    @BeforeEach
    void setUp() {
        transactionFactory = new ReversalTransactionFactory(merchantService, transactionRepository, referenceTransactionRepository);
    }


    @ParameterizedTest
    @CsvSource({"APPROVED, APPROVED, REVERSED", "REVERSED, ERROR, REVERSED"})
    void createTransaction_success(String refStatus, String resultStatus, String resultRefStatus) {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.ACTIVE);

        AuthorizeTransaction refTransaction = new AuthorizeTransaction();
        refTransaction.setStatus(TransactionStatus.valueOf(refStatus));
        refTransaction.setUuid(transactionInputDto.getReferenceId());
        refTransaction.setAmount(transactionInputDto.getAmount());

        when(transactionRepository.save(any(ReversalTransaction.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceTransactionRepository.findById(transactionInputDto.getReferenceId())).thenReturn(Optional.of(refTransaction));
        when(merchantService.findById(transactionInputDto.getMerchantId())).thenReturn(merchant);

        Transaction transaction = transactionFactory.createTransaction(transactionInputDto);

        verify(transactionRepository).save(any(ReversalTransaction.class));
        verify(referenceTransactionRepository).save(any(AuthorizeTransaction.class));
        assertEquals(transaction.getClass(), ReversalTransaction.class);
        assertEquals(transaction.getCustomerEmail(), transactionInputDto.getCustomerEmail());
        assertEquals(transaction.getCustomerPhone(), transactionInputDto.getCustomerPhone());
        assertEquals(transaction.getMerchant(), merchant);
        assertEquals(transaction.getReferenceTransaction(), refTransaction);
        assertNull(transaction.getAmount());
        assertEquals(transaction.getStatus(), TransactionStatus.valueOf(resultStatus));
        assertEquals(refTransaction.getStatus(), TransactionStatus.valueOf(resultRefStatus));
    }

    @Test
    void createTransaction_failure_refNotFound() {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.ACTIVE);
        when(merchantService.findById(transactionInputDto.getMerchantId())).thenReturn(merchant);
        when(referenceTransactionRepository.findById(transactionInputDto.getReferenceId())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionFactory.createTransaction(transactionInputDto));
    }

    @Test
    void createTransaction_failed_merchantInactive() {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.INACTIVE);
        when(merchantService.findById(transactionInputDto.getMerchantId())).thenReturn(merchant);

        assertThrows(MerchantInactiveException.class, () -> transactionFactory.createTransaction(transactionInputDto));
    }
}