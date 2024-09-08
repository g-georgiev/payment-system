package system.payments.poc.service;

import jakarta.transaction.Transactional;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;

import java.util.List;

public interface TransactionService {
    TransactionOutputDto createTransaction(TransactionInputDto transactionInputDto);

    List<TransactionOutputDto> getTransactions();

    @Transactional
    Integer cleanupTransactions(Integer hoursToKeep);
}
