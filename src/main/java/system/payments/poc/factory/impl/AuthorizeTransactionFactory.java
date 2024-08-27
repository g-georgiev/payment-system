package system.payments.poc.factory.impl;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.service.MerchantService;

import java.util.UUID;

@Service
public class AuthorizeTransactionFactory extends AbstractTransactionFactory {
    private final CrudRepository<AuthorizeTransaction, UUID> transactionRepository;

    public AuthorizeTransactionFactory(MerchantService merchantService,
                                       CrudRepository<AuthorizeTransaction, UUID> transactionRepository) {
        super(merchantService);
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInputDto transactionInputDto) {
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        populateCommonTransaction(authorizeTransaction, transactionInputDto);

        authorizeTransaction.setAmount(transactionInputDto.getAmount());
        authorizeTransaction.setStatus(TransactionStatus.APPROVED);

        return transactionRepository.save(authorizeTransaction);
    }

}
