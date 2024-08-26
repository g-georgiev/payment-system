package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.ReversalTransaction;

import java.util.UUID;

@Repository
public interface ReversalTransactionRepository extends CrudRepository<ReversalTransaction, UUID> {
}
