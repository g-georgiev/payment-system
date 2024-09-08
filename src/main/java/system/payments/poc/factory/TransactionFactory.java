package system.payments.poc.factory;

import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;

import java.util.Objects;
import java.util.Set;

@FunctionalInterface
public interface TransactionFactory {
    Transaction createTransaction(TransactionInputDto transactionInputDto, Merchant merchant, Transaction referenceTransaction);

    static void populateCommonTransaction(Transaction transaction, TransactionInputDto transactionInputDto) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();

        if (Objects.nonNull(referenceTransaction)) {
            transaction.setCustomerEmail(referenceTransaction.getCustomerEmail());
            transaction.setCustomerPhone(referenceTransaction.getCustomerPhone());
            transaction.setMerchant(referenceTransaction.getMerchant());
        } else {
            transaction.setCustomerEmail(transactionInputDto.getCustomerEmail());
            transaction.setCustomerPhone(transactionInputDto.getCustomerPhone());
        }
    }

    static void approveTransaction(Transaction referenceTransaction, Transaction transaction, Set<TransactionStatus> allowedStatuses) {
        if (allowedStatuses.contains(referenceTransaction.getStatus())) {
            transaction.setStatus(TransactionStatus.APPROVED);
        } else {
            transaction.setStatus(TransactionStatus.ERROR);
        }
    }
}
