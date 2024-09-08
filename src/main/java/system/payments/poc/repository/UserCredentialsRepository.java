package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import system.payments.poc.model.UserCredentials;

import java.util.Optional;

public interface UserCredentialsRepository extends CrudRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByUsername(String username);
}
