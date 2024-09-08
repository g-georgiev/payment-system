package system.payments.poc.factory.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static system.payments.poc.factory.TransactionTestUtil.generateTransactionInputDto;

@ExtendWith(MockitoExtension.class)
class RefundTransactionFactoryTest {
    private TransactionFactory transactionFactory;

    @BeforeEach
    void setUp() {
        transactionFactory = new RefundTransactionFactory();
    }


    @ParameterizedTest
    @CsvSource({"APPROVED, APPROVED", "REVERSED, ERROR"})
    void createTransaction_success(String refStatus, String resultStatus) {
        TransactionInputDto transactionInputDto = generateTransactionInputDto();

        ChargeTransaction refTransaction = new ChargeTransaction();
        refTransaction.setStatus(TransactionStatus.valueOf(refStatus));
        refTransaction.setUuid(transactionInputDto.getReferenceId());
        refTransaction.setAmount(transactionInputDto.getAmount());
        refTransaction.setMerchant(new Merchant());
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        refTransaction.setReferenceTransaction(authorizeTransaction);

        Transaction transaction = transactionFactory.createTransaction(transactionInputDto, null, refTransaction);

        assertEquals(RefundTransaction.class, transaction.getClass());
        assertEquals(refTransaction, transaction.getReferenceTransaction());
        assertEquals(refTransaction.getMerchant(), transaction.getMerchant());
        assertEquals(transactionInputDto.getAmount(), transaction.getAmount());
        assertEquals(TransactionStatus.valueOf(resultStatus), transaction.getStatus());
    }
}