package system.payments.poc.config;

import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.factory.impl.AuthorizeTransactionFactory;
import system.payments.poc.factory.impl.ChargeTransactionFactory;
import system.payments.poc.factory.impl.RefundTransactionFactory;
import system.payments.poc.factory.impl.ReversalTransactionFactory;

import java.util.Map;

import static system.payments.poc.enums.TransactionType.AUTHORIZE;
import static system.payments.poc.enums.TransactionType.CHARGE;
import static system.payments.poc.enums.TransactionType.REFUND;
import static system.payments.poc.enums.TransactionType.REVERSAL;

@Setter
@Configuration
public class BeanConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    Map<TransactionType, TransactionFactory> transactionFactoryMap() {
        return Map.of(AUTHORIZE, applicationContext.getBean(AuthorizeTransactionFactory.class),
                CHARGE, applicationContext.getBean(ChargeTransactionFactory.class),
                REFUND, applicationContext.getBean(RefundTransactionFactory.class),
                REVERSAL, applicationContext.getBean(ReversalTransactionFactory.class));
    }

}
