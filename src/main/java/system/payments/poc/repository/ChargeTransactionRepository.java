package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.ChargeTransaction;

import java.util.UUID;

@Repository
public interface ChargeTransactionRepository extends CrudRepository<ChargeTransaction, UUID> {

    boolean existsByUuid(UUID uuid);

    ChargeTransaction findByUuid(UUID uuid);
}
