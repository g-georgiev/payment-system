package system.payments.poc.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import system.payments.poc.enums.MerchantStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "merchant")
@Getter
@Setter
@NoArgsConstructor
public class Merchant extends UserCredentials {

    @Column
    private String name;

    @Column(unique = true)
    @Email
    private String email;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private MerchantStatus status = MerchantStatus.ACTIVE;

    @Column
    @PositiveOrZero
    private BigDecimal totalTransactionSum = BigDecimal.ZERO;

    @OneToMany(mappedBy = "merchant")
    private List<Transaction> transactions = new ArrayList<>();
}
