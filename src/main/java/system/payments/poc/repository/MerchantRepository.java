package system.payments.poc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
