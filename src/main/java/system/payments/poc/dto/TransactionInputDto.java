package system.payments.poc.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
@Builder
public class TransactionInputDto {

    @Email(message = "Customer email must have an email format")
    private String customerEmail;

    private String customerPhone;

    private Long merchantId;

    @Positive(message = "Amount must be grater than 0")
    private BigDecimal amount;
    private UUID referenceId;

    @AssertTrue(message = "Customer email and phone and the merchant id are mandatory when no reference id is provided")
    public boolean isDtoValid() {
        return nonNull(referenceId) || nonNull(merchantId) && nonNull(customerEmail) && nonNull(customerPhone);
    }
}
