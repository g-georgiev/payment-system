package system.payments.poc.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.model.Merchant;
import system.payments.poc.service.UserCredentialsService;
import system.payments.poc.strategy.TransactionValidationStrategy;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthorizeTransactionValidationStrategy implements TransactionValidationStrategy {
    protected final UserCredentialsService userCredentialsService;

    @Override
    public String validateTransaction(TransactionInputDto transaction) {
        Merchant merchant = (Merchant) userCredentialsService.getCurrentUserCredentials().getUserCredentials();
        if (!merchant.getStatus().equals(MerchantStatus.ACTIVE)) {
            return String.format("Merchant with id %s is not active", merchant.getId());
        }

        if (Objects.isNull(transaction.getAmount())) {
            return "Amount cannot be null for AUTHORIZE Transactions";
        }

        if (transaction.getAmount().signum() < 1) {
            return "Amount must be positive for AUTHORIZE Transactions";
        }

        if (Objects.nonNull(transaction.getReferenceId())) {
            return "ReferenceId should not be provided for AUTHORIZE Transactions";
        }

        return null;
    }
}
