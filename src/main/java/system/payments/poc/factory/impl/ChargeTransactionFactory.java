package system.payments.poc.factory.impl;

import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;

import java.util.Set;

import static system.payments.poc.factory.TransactionFactory.approveTransaction;
import static system.payments.poc.factory.TransactionFactory.populateCommonTransaction;

@Service
public class ChargeTransactionFactory implements TransactionFactory {

    @Override
    public Transaction createTransaction(TransactionInputDto transactionInputDto, Merchant merchant, Transaction referenceTransaction) {
        ChargeTransaction chargeTransaction = new ChargeTransaction();
        chargeTransaction.setReferenceTransaction(referenceTransaction);
        chargeTransaction.setAmount(referenceTransaction.getAmount());
        approveTransaction(referenceTransaction, chargeTransaction, Set.of(TransactionStatus.APPROVED));
        populateCommonTransaction(chargeTransaction, transactionInputDto);
        return chargeTransaction;
    }

}
