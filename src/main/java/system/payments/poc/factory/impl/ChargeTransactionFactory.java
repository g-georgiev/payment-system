package system.payments.poc.factory.impl;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.exceptions.TransactionNotFoundException;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Transaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.service.MerchantService;

import java.util.Set;
import java.util.UUID;

@Service
public class ChargeTransactionFactory extends AbstractTransactionFactory {
    private final CrudRepository<ChargeTransaction, UUID> transactionRepository;
    private final AuthorizeTransactionRepository referenceTransactionRepository;

    public ChargeTransactionFactory(MerchantService merchantService,
                                    CrudRepository<ChargeTransaction, UUID> transactionRepository,
                                    AuthorizeTransactionRepository referenceTransactionRepository) {
        super(merchantService);
        this.transactionRepository = transactionRepository;
        this.referenceTransactionRepository = referenceTransactionRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInputDto transactionInputDto) {
        ChargeTransaction chargeTransaction = new ChargeTransaction();
        populateCommonTransaction(chargeTransaction, transactionInputDto);

        AuthorizeTransaction referenceTransaction = referenceTransactionRepository.findByUuid(transactionInputDto.getReferenceId())
                .orElseThrow(TransactionNotFoundException::new);
        chargeTransaction.setReferenceTransaction(referenceTransaction);
        chargeTransaction.setAmount(referenceTransaction.getAmount());

        if (approveTransaction(referenceTransaction, chargeTransaction, Set.of(TransactionStatus.APPROVED))) {
            merchantService.updateTotalTransactionSum(chargeTransaction.getMerchant(), chargeTransaction.getAmount());
        }

        return transactionRepository.save(chargeTransaction);
    }

}
