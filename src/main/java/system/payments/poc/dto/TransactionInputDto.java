package system.payments.poc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.validator.ValidTransaction;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@ValidTransaction
public class TransactionInputDto {
    @NotNull
    private TransactionType transactionType;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal amount;
    private UUID referenceId;
}
