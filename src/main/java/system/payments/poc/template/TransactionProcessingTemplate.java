package system.payments.poc.template;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.service.UserCredentialsService;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class TransactionProcessingTemplate {
    private final UserCredentialsService userCredentialsService;

    private final TransactionFactory transactionFactory;

    private Merchant supplyMerchant() {
        return (Merchant) userCredentialsService.getCurrentUserCredentials().getUserCredentials();
    }

    protected Transaction supplyReference(UUID transactionId) {
        return null;
    }

    protected abstract void finalizeProcessing(Transaction transaction);

    @Transactional
    public Transaction process(TransactionInputDto transactionInputDto) {
        Transaction transaction = createTransaction(transactionInputDto);
        finalizeProcessing(transaction);
        return transaction;
    }

    private Transaction createTransaction(TransactionInputDto transactionInputDto) {
        UUID referenceId = transactionInputDto.getReferenceId();
        Merchant merchant = null;
        Transaction referenceTransaction = null;

        if (Objects.nonNull(referenceId)) {
            referenceTransaction = supplyReference(referenceId);
        } else {
            merchant = supplyMerchant();
        }

        return transactionFactory.createTransaction(transactionInputDto, merchant, referenceTransaction);
    }
}
