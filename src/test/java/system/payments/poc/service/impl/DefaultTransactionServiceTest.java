package system.payments.poc.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.mapper.TransactionOutputMapper;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.model.UserCredentials;
import system.payments.poc.model.security.UserSecurity;
import system.payments.poc.repository.TransactionRepository;
import system.payments.poc.service.TransactionService;
import system.payments.poc.service.UserCredentialsService;
import system.payments.poc.template.impl.AuthorizeTransactionProcessingTemplate;
import system.payments.poc.template.impl.ChargeTransactionProcessingTemplate;
import system.payments.poc.template.impl.RefundTransactionProcessingTemplate;
import system.payments.poc.template.impl.ReversalTransactionProcessingTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
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
    private AuthorizeTransactionProcessingTemplate authorizeTransactionProcessingTemplate;

    @Mock
    private ChargeTransactionProcessingTemplate chargeTransactionProcessingTemplate;

    @Mock
    private RefundTransactionProcessingTemplate refundTransactionProcessingTemplate;

    @Mock
    private ReversalTransactionProcessingTemplate reversalTransactionProcessingTemplate;

    @Mock
    private UserCredentialsService userCredentialsService;

    @BeforeEach
    void setUp() {
        transactionService = new DefaultTransactionService(transactionRepository,
                Map.of(AUTHORIZE, authorizeTransactionProcessingTemplate, CHARGE, chargeTransactionProcessingTemplate,
                        REFUND, refundTransactionProcessingTemplate, REVERSAL, reversalTransactionProcessingTemplate),
                transactionMapper, userCredentialsService);
    }

    @Test
    void createTransaction_success_authorize() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().transactionType(AUTHORIZE).build();

        transactionService.createTransaction(transactionInputDto);

        verify(authorizeTransactionProcessingTemplate).process(transactionInputDto);
    }

    @Test
    void createTransaction_success_charge() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().transactionType(CHARGE).referenceId(UUID.randomUUID()).build();

        transactionService.createTransaction(transactionInputDto);

        verify(chargeTransactionProcessingTemplate).process(transactionInputDto);
    }

    @Test
    void createTransaction_success_refund() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().transactionType(REFUND).referenceId(UUID.randomUUID()).build();

        transactionService.createTransaction(transactionInputDto);

        verify(refundTransactionProcessingTemplate).process(transactionInputDto);
    }

    @Test
    void createTransaction_success_reversal() {
        TransactionInputDto transactionInputDto = TransactionInputDto.builder().transactionType(REVERSAL).referenceId(UUID.randomUUID()).build();

        transactionService.createTransaction(transactionInputDto);

        verify(reversalTransactionProcessingTemplate).process(transactionInputDto);
    }

    @Test
    void getCurrentUserTransactions() {
        UserSecurity userSecurity = mock(UserSecurity.class);
        UserCredentials userCredentials = new Merchant();
        userCredentials.setId(1L);
        when(userCredentialsService.getCurrentUserCredentials()).thenReturn(userSecurity);
        when(userSecurity.getUserCredentials()).thenReturn(userCredentials);

        Transaction transaction = new AuthorizeTransaction();
        when(transactionRepository.findAllByMerchant_IdOrderByCreationDateDesc(userCredentials.getId())).thenReturn(List.of(transaction));

        transactionService.getCurrentUserTransactions();

        verify(transactionRepository).findAllByMerchant_IdOrderByCreationDateDesc(userCredentials.getId());
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void cleanupTransactions() {
        int hours = 1;
        LocalDateTime expectedDate = LocalDateTime.now().minusHours(hours).truncatedTo(ChronoUnit.SECONDS);

        transactionService.cleanupTransactions(hours);

        verify(transactionRepository).deleteByCreationDateBefore(expectedDate);
    }
}