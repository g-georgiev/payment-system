package system.payments.poc.factory;

import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.model.Transaction;

public interface TransactionFactory {
    Transaction createTransaction(TransactionInputDto transactionInputDto);
}
