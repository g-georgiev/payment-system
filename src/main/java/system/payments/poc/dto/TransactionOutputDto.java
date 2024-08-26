package system.payments.poc.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class TransactionOutputDto {

    private UUID uuid;

    private String customerEmail;

    private String customerPhone;

    private BigDecimal amount;

    private TransactionOutputDto referenceTransaction;
}
