package system.payments.poc.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.model.AuthorizeTransaction;
import system.payments.poc.repository.AuthorizeTransactionRepository;
import system.payments.poc.service.UserCredentialsService;
import system.payments.poc.strategy.TransactionValidationStrategy;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReversalTransactionValidationStrategy implements TransactionValidationStrategy {

    private final AuthorizeTransactionRepository referenceTransactionRepository;

    private final UserCredentialsService userCredentialsService;

    @Override
    public String validateTransaction(TransactionInputDto transaction) {
        UUID referenceId = transaction.getReferenceId();

        if (Objects.isNull(referenceId)) {
            return "Reference id is mandatory for REVERSAL transactions";
        }

        AuthorizeTransaction reference = referenceTransactionRepository.findByUuid(referenceId);
        if (Objects.isNull(reference)) {
            return "AUTHORIZE transaction reference id " + referenceId + " does not exist";
        }

        Long currentMerchantId = userCredentialsService.getCurrentUserCredentials().getUserCredentials().getId();
        if (!reference.getMerchant().getId().equals(currentMerchantId)) {
            return "AUTHORIZE transaction reference MUST belong to current merchant";
        }

        return null;
    }
}
