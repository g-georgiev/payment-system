package system.payments.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.mapper.TransactionOutputMapper;
import system.payments.poc.repository.TransactionRepository;
import system.payments.poc.service.TransactionService;
import system.payments.poc.service.UserCredentialsService;
import system.payments.poc.template.TransactionProcessingTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {
    private final TransactionRepository transactionRepository;

    private final Map<TransactionType, TransactionProcessingTemplate> transactionProcessingTemplates;

    private final TransactionOutputMapper transactionMapper;

    private final UserCredentialsService userCredentialsService;

    @Override
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public TransactionOutputDto createTransaction(TransactionInputDto transactionInputDto) {
        return transactionMapper.toDto(transactionProcessingTemplates.get(transactionInputDto.getTransactionType()).process(transactionInputDto));
    }

    @Override
    public List<TransactionOutputDto> getTransactions() {
        Long merchantId = userCredentialsService.getCurrentUserCredentials().getUserCredentials().getId();
        return transactionRepository.findAllByMerchant_Id(merchantId).stream().map(transactionMapper::toDto).toList();
    }

    @Transactional
    @Override
    public Integer cleanupTransactions(Integer hoursToKeep) {
        LocalDateTime deleteDate = LocalDateTime.now().minusHours(hoursToKeep).truncatedTo(ChronoUnit.SECONDS);
        return transactionRepository.deleteByCreationDateBefore(deleteDate);
    }
}
