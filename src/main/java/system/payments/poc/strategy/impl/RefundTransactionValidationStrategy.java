package system.payments.poc.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.strategy.TransactionValidationStrategy;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefundTransactionValidationStrategy implements TransactionValidationStrategy {

    private final ChargeTransactionRepository referenceTransactionRepository;

    @Override
    public String validateTransaction(TransactionInputDto transaction) {
        UUID referenceId = transaction.getReferenceId();

        if (Objects.isNull(referenceId)) {
            return "Reference id is mandatory for REFUND transactions";
        }

        if (!referenceTransactionRepository.existsByUuid(referenceId)) {
            return "CHARGE transaction reference id " + referenceId + " does not exist";
        }

        return null;
    }
}
