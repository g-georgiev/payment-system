package system.payments.poc.factory.impl;

import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.RefundTransaction;
import system.payments.poc.model.Transaction;

import java.util.Set;

import static system.payments.poc.factory.TransactionFactory.approveTransaction;
import static system.payments.poc.factory.TransactionFactory.populateCommonTransaction;

@Service
public class RefundTransactionFactory implements TransactionFactory {

    @Override
    public Transaction createTransaction(TransactionInputDto transactionInputDto, Merchant merchant, Transaction referenceTransaction) {
        RefundTransaction transaction = new RefundTransaction();
        transaction.setReferenceTransaction(referenceTransaction);
        transaction.setAmount(referenceTransaction.getAmount());
        populateCommonTransaction(transaction, transactionInputDto);
        approveTransaction(referenceTransaction, transaction, Set.of(TransactionStatus.APPROVED));
        return transaction;
    }

}
