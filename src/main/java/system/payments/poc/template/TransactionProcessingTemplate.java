package system.payments.poc.template;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.service.MerchantService;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class TransactionProcessingTemplate {
    protected final MerchantService merchantService;

    private final TransactionFactory transactionFactory;

    protected Merchant supplyMerchant(Long merchantId) {
        return merchantService.findById(merchantId);
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
            merchant = supplyMerchant(transactionInputDto.getMerchantId());
        }

        return transactionFactory.createTransaction(transactionInputDto, merchant, referenceTransaction);
    }
}
