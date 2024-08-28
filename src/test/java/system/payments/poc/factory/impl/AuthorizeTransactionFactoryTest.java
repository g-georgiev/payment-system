package system.payments.poc.factory.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.exceptions.MerchantInactiveException;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.service.MerchantService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static system.payments.poc.factory.TransactionTestUtil.generateTransactionInputDto;

@ExtendWith(MockitoExtension.class)
class AuthorizeTransactionFactoryTest {
    private TransactionFactory transactionFactory;

    @Mock
    private MerchantService merchantService;

    @Mock
    private AuthorizeTransactionRepository authorizeTransactionRepository;

    @BeforeEach
    void setUp() {
        transactionFactory = new AuthorizeTransactionFactory(merchantService, authorizeTransactionRepository);
    }

    @Test
    void createTransaction() {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.ACTIVE);
        when(authorizeTransactionRepository.save(any(AuthorizeTransaction.class))).thenAnswer(i -> i.getArgument(0));
        when(merchantService.findById(transactionInputDto.getMerchantId())).thenReturn(merchant);

        Transaction transaction = transactionFactory.createTransaction(transactionInputDto);

        verify(authorizeTransactionRepository).save(any(AuthorizeTransaction.class));
        assertEquals(AuthorizeTransaction.class, transaction.getClass());
        assertEquals(transactionInputDto.getCustomerEmail(), transaction.getCustomerEmail());
        assertEquals(transactionInputDto.getCustomerPhone(), transaction.getCustomerPhone());
        assertEquals(merchant, transaction.getMerchant());
        assertEquals(transactionInputDto.getAmount(), transaction.getAmount());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertNull(transaction.getReferenceTransaction());
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