package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.RefundTransaction;

import java.util.UUID;

@Repository
public interface RefundTransactionRepository extends CrudRepository<RefundTransaction, UUID> {
}
