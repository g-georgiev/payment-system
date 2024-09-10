package system.payments.poc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import system.payments.poc.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionOutputDto {

    private UUID uuid;

    private String transactionType;

    private TransactionStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    private String customerEmail;

    private String customerPhone;

    private BigDecimal amount;

    private UUID referenceTransactionUuid;
}
