package system.payments.poc.factory.impl;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.service.MerchantService;

import java.util.UUID;

@Service
public class RefundTransactionFactory extends AbstractTransactionFactory {
    private final CrudRepository<RefundTransaction, UUID> transactionRepository;
    private final CrudRepository<ChargeTransaction, UUID> referenceTransactionRepository;

    public RefundTransactionFactory(MerchantService merchantService,
                                    CrudRepository<RefundTransaction, UUID> transactionRepository,
                                    CrudRepository<ChargeTransaction, UUID> referenceTransactionRepository) {
        super(merchantService);
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInputDto transactionInputDto) {
        RefundTransaction transaction = new RefundTransaction();
        populateCommonTransaction(transaction, transactionInputDto);

        ChargeTransaction referenceTransaction = referenceTransactionRepository.findById(transactionInputDto.getReferenceId())
                .orElseThrow(TransactionNotFoundException::new);
        transaction.setReferenceTransaction(referenceTransaction);
        transaction.setAmount(referenceTransaction.getAmount());

        if (approveTransaction(referenceTransaction, transaction)) {
            merchantService.updateTotalTransactionSum(transaction.getMerchant(), transaction.getAmount().negate());
            referenceTransaction.setStatus(TransactionStatus.REFUNDED);
        }

        referenceTransactionRepository.save(referenceTransaction);
        return transactionRepository.save(transaction);
    }

}
