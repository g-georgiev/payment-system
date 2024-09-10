package system.payments.poc.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.model.ChargeTransaction;
import system.payments.poc.repository.ChargeTransactionRepository;
import system.payments.poc.service.UserCredentialsService;
import system.payments.poc.strategy.TransactionValidationStrategy;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefundTransactionValidationStrategy implements TransactionValidationStrategy {

    private final ChargeTransactionRepository referenceTransactionRepository;

    private final UserCredentialsService userCredentialsService;

    @Override
    public String validateTransaction(TransactionInputDto transaction) {
        UUID referenceId = transaction.getReferenceId();

        if (Objects.isNull(referenceId)) {
            return "Reference id is mandatory for REFUND transactions";
        }

        ChargeTransaction reference = referenceTransactionRepository.findByUuid(referenceId);
        if (Objects.isNull(reference)) {
            return "CHARGE transaction reference id " + referenceId + " does not exist";
        }

        Long currentMerchantId = userCredentialsService.getCurrentUserCredentials().getUserCredentials().getId();
        if (!reference.getMerchant().getId().equals(currentMerchantId)) {
            return "CHARGE transaction reference MUST belong to current merchant";
        }

        return null;
    }
}
