package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.AuthorizeTransaction;

import java.util.UUID;

@Repository
public interface AuthorizeTransactionRepository extends CrudRepository<AuthorizeTransaction, UUID> {
}
