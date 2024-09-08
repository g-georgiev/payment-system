package system.payments.poc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminDto extends UserDTO {
    private boolean isActive;
}
