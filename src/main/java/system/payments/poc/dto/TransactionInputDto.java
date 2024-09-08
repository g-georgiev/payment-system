package system.payments.poc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @Email(message = "Customer email must have an email format")
    private String customerEmail;

    private String customerPhone;

    private Long merchantId;

    @Positive(message = "Amount must be grater than 0")
    private BigDecimal amount;
    private UUID referenceId;
}
