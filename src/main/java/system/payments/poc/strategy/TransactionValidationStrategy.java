package system.payments.poc.strategy;

import system.payments.poc.dto.TransactionInputDto;

@FunctionalInterface
public interface TransactionValidationStrategy {
    String validateTransaction(TransactionInputDto transaction);
}
