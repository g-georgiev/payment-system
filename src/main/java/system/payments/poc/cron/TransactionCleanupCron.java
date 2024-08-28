package system.payments.poc.cron;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import system.payments.poc.service.TransactionService;

@Component
@RequiredArgsConstructor
@Setter
public class TransactionCleanupCron {

    private final Logger LOG = LoggerFactory.getLogger(TransactionCleanupCron.class);
    private final TransactionService transactionService;

    @Value("${payments.retention.period}")
    private Integer PAYMENT_RETENTION_PERIOD;

    // Every hour
    @Scheduled(cron = "0 0 * * * *")
    public void cleanUpTransactions() {
        LOG.info("Starting scheduler - 'cleanUpTransactions'");

        Integer deletedTransactions = transactionService.cleanupTransactions(PAYMENT_RETENTION_PERIOD);

        LOG.info("Successfully executed - 'cleanUpTransactions'");
        LOG.info("Deleted transactions: {}", deletedTransactions);
    }
}
