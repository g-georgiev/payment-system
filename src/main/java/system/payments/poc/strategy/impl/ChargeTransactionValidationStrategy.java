package system.payments.poc.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.strategy.TransactionValidationStrategy;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChargeTransactionValidationStrategy implements TransactionValidationStrategy {

    private final AuthorizeTransactionRepository referenceTransactionRepository;

    @Override
    public String validateTransaction(TransactionInputDto transaction) {
        UUID referenceId = transaction.getReferenceId();

        if (Objects.isNull(referenceId)) {
            return "Reference id is mandatory for CHARGE transactions";
        }

        if (!referenceTransactionRepository.existsByUuid(referenceId)) {
            return "AUTHORIZE transaction reference id " + referenceId + " does not exist";
        }

        return null;
    }
}
