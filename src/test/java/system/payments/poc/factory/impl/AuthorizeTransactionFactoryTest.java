package system.payments.poc.factory.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static system.payments.poc.factory.TransactionTestUtil.generateTransactionInputDto;

@ExtendWith(MockitoExtension.class)
class AuthorizeTransactionFactoryTest {
    private TransactionFactory transactionFactory;


    @BeforeEach
    void setUp() {
        transactionFactory = new AuthorizeTransactionFactory();
    }

    @Test
    void createTransaction() {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();
        Merchant merchant = new Merchant();
        merchant.setStatus(MerchantStatus.ACTIVE);

        Transaction transaction = transactionFactory.createTransaction(transactionInputDto, merchant, null);

        assertEquals(AuthorizeTransaction.class, transaction.getClass());
        assertEquals(transactionInputDto.getCustomerEmail(), transaction.getCustomerEmail());
        assertEquals(transactionInputDto.getCustomerPhone(), transaction.getCustomerPhone());
        assertEquals(merchant, transaction.getMerchant());
        assertEquals(transactionInputDto.getAmount(), transaction.getAmount());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertNull(transaction.getReferenceTransaction());
    }
}