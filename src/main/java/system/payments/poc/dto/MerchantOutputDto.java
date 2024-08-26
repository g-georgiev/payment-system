package system.payments.poc.dto;

import lombok.Builder;
import lombok.Data;
import system.payments.poc.enums.MerchantStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MerchantOutputDto {

    private String name;

    private String email;

    private String description;

    private MerchantStatus status;

    private BigDecimal totalTransactionSum;

    private List<TransactionOutputDto> transactions;

}
