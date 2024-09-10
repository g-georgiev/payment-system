package system.payments.poc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import system.payments.poc.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {

    List<Transaction> findAllByMerchant_IdOrderByCreationDateDesc(Long merchantId);

    boolean existsByMerchant_Id(Long merchant_id);

    int deleteByCreationDateBefore(LocalDateTime createdAt);
}
