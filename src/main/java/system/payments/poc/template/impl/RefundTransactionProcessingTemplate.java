package system.payments.poc.template.impl;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.repository.RefundTransactionRepository;
import system.payments.poc.service.MerchantService;
import system.payments.poc.service.UserCredentialsService;
import system.payments.poc.template.TransactionProcessingTemplate;

import java.util.UUID;

@Component
public class RefundTransactionProcessingTemplate extends TransactionProcessingTemplate {

    private final MerchantService merchantService;
    private final RefundTransactionRepository transactionRepository;
    private final ChargeTransactionRepository referenceTransactionRepository;
    private final AuthorizeTransactionRepository authorizeTransactionRepository;

    public RefundTransactionProcessingTemplate(MerchantService merchantService,
                                               UserCredentialsService userCredentialsService,
                                               TransactionFactory refundTransactionFactory,
                                               RefundTransactionRepository transactionRepository,
                                               ChargeTransactionRepository referenceTransactionRepository,
                                               AuthorizeTransactionRepository authorizeTransactionRepository) {
        super(userCredentialsService, refundTransactionFactory);
        this.merchantService = merchantService;
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
        this.authorizeTransactionRepository = authorizeTransactionRepository;
    }


    @Override
    protected Transaction supplyReference(UUID transactionId) {
        return referenceTransactionRepository.findByUuid(transactionId);
    }

    @Override
    @Transactional
    protected void finalizeProcessing(Transaction transaction) {
        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            merchantService.updateTotalTransactionSum(transaction.getMerchant(), transaction.getAmount().negate());

            Transaction referenceTransaction = transaction.getReferenceTransaction();
            referenceTransaction.setStatus(TransactionStatus.REFUNDED);

            Transaction authorizeTransaction = referenceTransaction.getReferenceTransaction();
            if (authorizeTransaction instanceof HibernateProxy) {
                authorizeTransaction = (AuthorizeTransaction) Hibernate.unproxy(authorizeTransaction);
            }
            authorizeTransaction.setStatus(TransactionStatus.REFUNDED);
            authorizeTransactionRepository.save((AuthorizeTransaction) authorizeTransaction);
            referenceTransactionRepository.save((ChargeTransaction) referenceTransaction);
        }

        transactionRepository.save((RefundTransaction) transaction);
    }
}
