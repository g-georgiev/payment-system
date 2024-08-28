package system.payments.poc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class TransactionInputDto {

    @NotNull(message = "Customer email must not be null")
    @Email(message = "Customer email must have an email format")
    @NotEmpty(message = "Customer email must not be empty")
    private String customerEmail;

    @NotNull(message = "Customer phone must not be null")
    @NotEmpty(message = "Customer phone must not be empty")
    private String customerPhone;

    @NotNull(message = "Merchant id must not be null")
    private Long merchantId;

    @Positive(message = "Amount must be grater than 0")
    private BigDecimal amount;
    private UUID referenceId;
}
