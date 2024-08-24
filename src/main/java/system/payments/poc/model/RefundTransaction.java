package system.payments.poc.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class RefundTransaction extends Transaction {
    @PositiveOrZero
    private BigDecimal amount = BigDecimal.ZERO;
}
