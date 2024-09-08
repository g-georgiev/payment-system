package system.payments.poc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import system.payments.poc.enums.MerchantStatus;

@Data
@Builder
public class MerchantInputDto {

    @NotNull(message = "Merchant username must not be null")
    @NotEmpty(message = "Merchant username must not be empty")
    private String username;

    @NotNull(message = "Merchant password must not be null")
    @NotEmpty(message = "Merchant password must not be empty")
    private String password;

    @NotNull(message = "Merchant name must not be null")
    @NotEmpty(message = "Merchant name must not be empty")
    private String name;

    @NotNull(message = "Merchant email must not be null")
    @Email(message = "Merchant email must have an email format")
    @NotEmpty(message = "Merchant email must not be empty")
    private String email;

    @NotNull(message = "Merchant description must not be null")
    @NotEmpty(message = "Merchant description must not be empty")
    private String description;

    private MerchantStatus status;
}
