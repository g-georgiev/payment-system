package system.payments.poc.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;

import java.util.List;

public interface TransactionService {
    TransactionOutputDto createTransaction(TransactionInputDto transactionInputDto);

    List<TransactionOutputDto> getCurrentUserTransactions();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<TransactionOutputDto> getTransactions(Long merchantId);

    @Transactional
    Integer cleanupTransactions(Integer hoursToKeep);
}
