package system.payments.poc.service;

import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.enums.TransactionType;

import java.util.List;

public interface TransactionService {
    TransactionOutputDto createTransaction(TransactionType transactionType, TransactionInputDto transactionInputDto);

    List<TransactionOutputDto> getTransactions(Long merchantId);
}
