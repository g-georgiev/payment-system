package system.payments.poc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
