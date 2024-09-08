package system.payments.poc.template.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.service.MerchantService;
import system.payments.poc.template.TransactionProcessingTemplate;

import java.util.UUID;

@Component
public class ChargeTransactionProcessingTemplate extends TransactionProcessingTemplate {

    private final ChargeTransactionRepository transactionRepository;
    private final AuthorizeTransactionRepository referenceTransactionRepository;

    public ChargeTransactionProcessingTemplate(MerchantService merchantService,
                                               TransactionFactory chargeTransactionFactory,
                                               ChargeTransactionRepository transactionRepository,
                                               AuthorizeTransactionRepository referenceTransactionRepository) {
        super(merchantService, chargeTransactionFactory);
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
            merchantService.updateTotalTransactionSum(transaction.getMerchant(), transaction.getAmount());
        }

        transactionRepository.save((ChargeTransaction) transaction);
    }
}
