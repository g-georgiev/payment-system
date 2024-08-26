package system.payments.poc.factory.impl;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ReversalTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.service.MerchantService;

import java.util.UUID;

@Service
public class ReversalTransactionFactory extends AbstractTransactionFactory {
    private final CrudRepository<ReversalTransaction, UUID> transactionRepository;
    private final CrudRepository<AuthorizeTransaction, UUID> referenceTransactionRepository;

    public ReversalTransactionFactory(MerchantService merchantService,
                                      CrudRepository<ReversalTransaction, UUID> transactionRepository,
                                      CrudRepository<AuthorizeTransaction, UUID> referenceTransactionRepository) {
        super(merchantService);
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInputDto transactionInputDto) {
        ReversalTransaction transaction = new ReversalTransaction();
        populateCommonTransaction(transaction, transactionInputDto);

        AuthorizeTransaction referenceTransaction = referenceTransactionRepository.findById(transactionInputDto.getReferenceId())
                .orElseThrow(TransactionNotFoundException::new);
        transaction.setReferenceTransaction(referenceTransaction);

        if (approveTransaction(referenceTransaction, transaction)) {
            referenceTransaction.setStatus(TransactionStatus.REVERSED);
        }

        referenceTransactionRepository.save(referenceTransaction);
        return transactionRepository.save(transaction);
    }
}
