package system.payments.poc.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import system.payments.poc.model.enums.MerchantStatus;

import java.math.BigDecimal;
import java.util.List;

@Entity(name = "merchant")
@Getter
@Setter
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status;

    @Column(nullable = false)
    @PositiveOrZero
    private BigDecimal totalTransactionSum = BigDecimal.ZERO;

    @OneToMany(mappedBy = "merchant")
    List<Transaction> transactions;
}
