package system.payments.poc.factory.impl;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.service.MerchantService;

import java.util.Set;
import java.util.UUID;

@Service
public class RefundTransactionFactory extends AbstractTransactionFactory {
    private final CrudRepository<RefundTransaction, UUID> transactionRepository;
    private final ChargeTransactionRepository referenceTransactionRepository;
    private final AuthorizeTransactionRepository authorizeTransactionRepository;

    public RefundTransactionFactory(MerchantService merchantService,
                                    CrudRepository<RefundTransaction, UUID> transactionRepository,
                                    ChargeTransactionRepository referenceTransactionRepository, AuthorizeTransactionRepository authorizeTransactionRepository) {
        super(merchantService);
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
        this.authorizeTransactionRepository = authorizeTransactionRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInputDto transactionInputDto) {
        RefundTransaction transaction = new RefundTransaction();
        populateCommonTransaction(transaction, transactionInputDto);

        ChargeTransaction referenceTransaction = referenceTransactionRepository.findByUuid(transactionInputDto.getReferenceId())
                .orElseThrow(TransactionNotFoundException::new);
        transaction.setReferenceTransaction(referenceTransaction);
        transaction.setAmount(referenceTransaction.getAmount());

        if (approveTransaction(referenceTransaction, transaction, Set.of(TransactionStatus.APPROVED))) {
            merchantService.updateTotalTransactionSum(transaction.getMerchant(), transaction.getAmount().negate());
            referenceTransaction.setStatus(TransactionStatus.REFUNDED);

            Transaction authorizeTransaction = referenceTransaction.getReferenceTransaction();
            if (authorizeTransaction instanceof HibernateProxy) {
                authorizeTransaction = (AuthorizeTransaction) Hibernate.unproxy(authorizeTransaction);
            }
            authorizeTransaction.setStatus(TransactionStatus.REFUNDED);
            authorizeTransactionRepository.save((AuthorizeTransaction) authorizeTransaction);
        }

        referenceTransactionRepository.save(referenceTransaction);
        return transactionRepository.save(transaction);
    }

}
