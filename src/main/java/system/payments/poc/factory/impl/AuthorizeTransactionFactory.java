package system.payments.poc.factory.impl;

import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;

import static system.payments.poc.factory.TransactionFactory.populateCommonTransaction;

@Service
public class AuthorizeTransactionFactory implements TransactionFactory {

    @Override
    public Transaction createTransaction(TransactionInputDto transactionInputDto, Merchant merchant, Transaction referenceTransaction) {
        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        authorizeTransaction.setMerchant(merchant);
        authorizeTransaction.setAmount(transactionInputDto.getAmount());
        authorizeTransaction.setStatus(TransactionStatus.APPROVED);
        populateCommonTransaction(authorizeTransaction, transactionInputDto);
        return authorizeTransaction;
    }

}
