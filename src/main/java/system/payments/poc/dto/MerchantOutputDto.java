package system.payments.poc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import system.payments.poc.enums.MerchantStatus;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantOutputDto extends UserDTO {

    private String name;

    private String email;

    private String description;

    private MerchantStatus status;

    private BigDecimal totalTransactionSum;

}
