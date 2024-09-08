package system.payments.poc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.Merchant;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByEmail(String email);

    Optional<Merchant> findByEmail(String email);
}
