package system.payments.poc.template.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.service.MerchantService;
import system.payments.poc.template.TransactionProcessingTemplate;

@Component
public class AuthorizeTransactionProcessingTemplate extends TransactionProcessingTemplate {
    private final AuthorizeTransactionRepository transactionRepository;

    public AuthorizeTransactionProcessingTemplate(MerchantService merchantService,
                                                  TransactionFactory authorizeTransactionFactory,
                                                  AuthorizeTransactionRepository transactionRepository) {
        super(merchantService, authorizeTransactionFactory);
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    protected void finalizeProcessing(Transaction transaction) {
        transactionRepository.save((AuthorizeTransaction) transaction);
    }
}
