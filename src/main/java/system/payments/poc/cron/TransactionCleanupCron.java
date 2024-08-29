package system.payments.poc.cron;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import system.payments.poc.service.TransactionService;

@Component
@RequiredArgsConstructor
@Setter
@Slf4j
public class TransactionCleanupCron {

    private final TransactionService transactionService;

    @Value("${payments.retention.period}")
    private Integer PAYMENT_RETENTION_PERIOD;

    // Every hour
    @Scheduled(cron = "0 0 * * * *")
    public void cleanUpTransactions() {
        log.info("Starting scheduler - 'cleanUpTransactions'");

        Integer deletedTransactions = transactionService.cleanupTransactions(PAYMENT_RETENTION_PERIOD);

        log.info("Successfully executed - 'cleanUpTransactions'");
        log.info("Deleted transactions: {}", deletedTransactions);
    }
}
