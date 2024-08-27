package system.payments.poc.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.factory.impl.AuthorizeTransactionFactory;
import system.payments.poc.factory.impl.ChargeTransactionFactory;
import system.payments.poc.factory.impl.RefundTransactionFactory;
import system.payments.poc.factory.impl.ReversalTransactionFactory;
import system.payments.poc.mapper.TransactionOutputMapper;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.TransactionRepository;
import system.payments.poc.service.TransactionService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static system.payments.poc.enums.TransactionType.AUTHORIZE;
import static system.payments.poc.enums.TransactionType.CHARGE;
import static system.payments.poc.enums.TransactionType.REFUND;
import static system.payments.poc.enums.TransactionType.REVERSAL;

@ExtendWith(MockitoExtension.class)
class DefaultTransactionServiceTest {

    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionOutputMapper transactionMapper;

    @Mock
    private AuthorizeTransactionFactory authorizeTransactionFactory;

    @Mock
    private ChargeTransactionFactory chargeTransactionFactory;

    @Mock
    private RefundTransactionFactory refundTransactionFactory;

    @Mock
    private ReversalTransactionFactory reversalTransactionFactory;

    @BeforeEach
    void setUp() {
        transactionService = new DefaultTransactionService(transactionRepository,
                Map.of(AUTHORIZE, authorizeTransactionFactory, CHARGE, chargeTransactionFactory,
                        REFUND, refundTransactionFactory, REVERSAL, reversalTransactionFactory),
                transactionMapper);
    }

    @Test
    void createTransaction_failed_illegalArgument() {
        TransactionInputDto transactionDto = TransactionInputDto.builder().build();

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(CHARGE, transactionDto));
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(REFUND, transactionDto));
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(REVERSAL, transactionDto));
    }

    @Test
    void createTransaction_success_authorize() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().build();

        transactionService.createTransaction(AUTHORIZE, transactionInputDto);

        verify(authorizeTransactionFactory).createTransaction(transactionInputDto);
    }

    @Test
    void createTransaction_success_charge() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().referenceId(UUID.randomUUID()).build();

        transactionService.createTransaction(CHARGE, transactionInputDto);

        verify(chargeTransactionFactory).createTransaction(transactionInputDto);
    }

    @Test
    void createTransaction_success_refund() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().referenceId(UUID.randomUUID()).build();

        transactionService.createTransaction(REFUND, transactionInputDto);

        verify(refundTransactionFactory).createTransaction(transactionInputDto);
    }

    @Test
    void createTransaction_success_reversal() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().referenceId(UUID.randomUUID()).build();

        transactionService.createTransaction(REVERSAL, transactionInputDto);

        verify(reversalTransactionFactory).createTransaction(transactionInputDto);
    }

    @Test
    void getTransactions() {
        Long merchantId = 1L;
        Transaction transaction = new AuthorizeTransaction();
        when(transactionRepository.findAllByMerchant_Id(merchantId)).thenReturn(List.of(transaction));

        transactionService.getTransactions(merchantId);

        verify(transactionRepository).findAllByMerchant_Id(merchantId);
        verify(transactionMapper).toDto(transaction);
    }
}