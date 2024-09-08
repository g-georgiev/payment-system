package system.payments.poc.factory.impl;

import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.ReversalTransaction;
import system.payments.poc.model.Transaction;

import java.util.Set;

import static system.payments.poc.factory.TransactionFactory.approveTransaction;
import static system.payments.poc.factory.TransactionFactory.populateCommonTransaction;

@Service
public class ReversalTransactionFactory implements TransactionFactory {

    @Override
    public Transaction createTransaction(TransactionInputDto transactionInputDto, Merchant merchant, Transaction referenceTransaction) {
        ReversalTransaction transaction = new ReversalTransaction();
        transaction.setReferenceTransaction(referenceTransaction);
        populateCommonTransaction(transaction, transactionInputDto);
        approveTransaction(referenceTransaction, transaction, Set.of(TransactionStatus.APPROVED, TransactionStatus.REFUNDED));
        return transaction;
    }
}
