package system.payments.poc.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReversalTransaction extends Transaction {
}
