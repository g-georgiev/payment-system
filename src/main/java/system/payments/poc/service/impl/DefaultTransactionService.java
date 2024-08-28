package system.payments.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.mapper.TransactionOutputMapper;
import system.payments.poc.repository.TransactionRepository;
import system.payments.poc.service.TransactionService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {
    private final TransactionRepository transactionRepository;

    private final Map<TransactionType, TransactionFactory> transactionFactories;

    private final TransactionOutputMapper transactionMapper;

    @Override
    public TransactionOutputDto createTransaction(TransactionType transactionType, TransactionInputDto transactionInputDto) {
        if (!transactionType.equals(TransactionType.AUTHORIZE) && Objects.isNull(transactionInputDto.getReferenceId())) {
            throw new IllegalArgumentException("Reference id is mandatory for transaction types other than AUTHORIZE");
        }

        return transactionMapper.toDto(transactionFactories.get(transactionType).createTransaction(transactionInputDto));
    }

    @Override
    public List<TransactionOutputDto> getTransactions(Long merchantId) {
        return transactionRepository.findAllByMerchant_Id(merchantId).stream().map(transactionMapper::toDto).toList();
    }

    @Transactional
    @Override
    public Integer cleanupTransactions(Integer hoursToKeep) {
        LocalDateTime deleteDate = LocalDateTime.now().minusHours(hoursToKeep).truncatedTo(ChronoUnit.SECONDS);
        return transactionRepository.deleteByCreationDateBefore(deleteDate);
    }
}
