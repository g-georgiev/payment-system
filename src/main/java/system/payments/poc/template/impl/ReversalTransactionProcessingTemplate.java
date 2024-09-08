package system.payments.poc.template.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ReversalTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ReversalTransactionRepository;
import system.payments.poc.service.MerchantService;
import system.payments.poc.template.TransactionProcessingTemplate;

import java.util.UUID;

@Component
public class ReversalTransactionProcessingTemplate extends TransactionProcessingTemplate {

    private final ReversalTransactionRepository transactionRepository;
    private final AuthorizeTransactionRepository referenceTransactionRepository;

    public ReversalTransactionProcessingTemplate(MerchantService merchantService,
                                                 TransactionFactory reversalTransactionFactory,
                                                 ReversalTransactionRepository transactionRepository,
                                                 AuthorizeTransactionRepository referenceTransactionRepository) {
        super(merchantService, reversalTransactionFactory);
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
    }


    @Override
    protected Transaction supplyReference(UUID transactionId) {
        return referenceTransactionRepository.findByUuid(transactionId);
    }

    @Override
    @Transactional
    protected void finalizeProcessing(Transaction transaction) {
        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            Transaction referenceTransaction = transaction.getReferenceTransaction();
            referenceTransaction.setStatus(TransactionStatus.REVERSED);
            referenceTransactionRepository.save((AuthorizeTransaction) referenceTransaction);
        }

        transactionRepository.save((ReversalTransaction) transaction);
    }
}
