package system.payments.poc.factory;

import system.payments.poc.dto.TransactionInputDto;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionTestUtil {
    public static TransactionInputDto generateTransactionInputDto() {
        return TransactionInputDto.builder()
                .customerEmail("test@test.com")
                .customerPhone("1234567890")
                .referenceId(UUID.randomUUID())
                .amount(BigDecimal.TEN)
                .build();
    }
}
