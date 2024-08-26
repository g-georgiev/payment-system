package system.payments.poc.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import system.payments.poc.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "transaction")
@Inheritance
@DiscriminatorColumn(name = "transaction_type")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    @Email
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "reference_id", nullable = false)
    private Transaction referenceTransaction;

    @OneToMany(mappedBy = "referenceTransaction")
    private List<Transaction> referencingTransactions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    private Merchant merchant;

    public BigDecimal getAmount() {
        return null;
    }
}
