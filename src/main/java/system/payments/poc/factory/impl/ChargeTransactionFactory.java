package system.payments.poc.factory.impl;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.service.MerchantService;

import java.util.UUID;

@Service
public class ChargeTransactionFactory extends AbstractTransactionFactory {
    private final CrudRepository<ChargeTransaction, UUID> transactionRepository;
    private final CrudRepository<AuthorizeTransaction, UUID> referenceTransactionRepository;

    public ChargeTransactionFactory(MerchantService merchantService,
                                    CrudRepository<ChargeTransaction, UUID> transactionRepository,
                                    CrudRepository<AuthorizeTransaction, UUID> referenceTransactionRepository) {
        super(merchantService);
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInputDto transactionInputDto) {
        ChargeTransaction chargeTransaction = new ChargeTransaction();
        populateCommonTransaction(chargeTransaction, transactionInputDto);

        AuthorizeTransaction referenceTransaction = referenceTransactionRepository.findById(transactionInputDto.getReferenceId())
                .orElseThrow(TransactionNotFoundException::new);
        chargeTransaction.setReferenceTransaction(referenceTransaction);
        chargeTransaction.setAmount(referenceTransaction.getAmount());

        if (approveTransaction(referenceTransaction, chargeTransaction)) {
            merchantService.updateTotalTransactionSum(chargeTransaction.getMerchant(), chargeTransaction.getAmount());
        }

        return transactionRepository.save(chargeTransaction);
    }

}
