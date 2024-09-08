package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.Admin;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Long> {
    Admin findByUsername(String username);

    Boolean existsByUsername(String username);
}
