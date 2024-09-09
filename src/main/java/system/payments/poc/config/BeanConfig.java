package system.payments.poc.config;

import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.strategy.TransactionValidationStrategy;
import system.payments.poc.strategy.impl.AuthorizeTransactionValidationStrategy;
import system.payments.poc.strategy.impl.ChargeTransactionValidationStrategy;
import system.payments.poc.strategy.impl.RefundTransactionValidationStrategy;
import system.payments.poc.strategy.impl.ReversalTransactionValidationStrategy;
import system.payments.poc.template.TransactionProcessingTemplate;
import system.payments.poc.template.impl.AuthorizeTransactionProcessingTemplate;
import system.payments.poc.template.impl.ChargeTransactionProcessingTemplate;
import system.payments.poc.template.impl.RefundTransactionProcessingTemplate;
import system.payments.poc.template.impl.ReversalTransactionProcessingTemplate;

import java.util.Map;

import static org.springframework.http.HttpMethod.*;
import static system.payments.poc.enums.TransactionType.AUTHORIZE;
import static system.payments.poc.enums.TransactionType.CHARGE;
import static system.payments.poc.enums.TransactionType.REFUND;
import static system.payments.poc.enums.TransactionType.REVERSAL;

@EnableWebMvc
@Setter
@Configuration
public class BeanConfig implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext applicationContext;

    @Bean
    Map<TransactionType, TransactionProcessingTemplate> transactionProcessingMap() {
        return Map.of(AUTHORIZE, applicationContext.getBean(AuthorizeTransactionProcessingTemplate.class),
                CHARGE, applicationContext.getBean(ChargeTransactionProcessingTemplate.class),
                REFUND, applicationContext.getBean(RefundTransactionProcessingTemplate.class),
                REVERSAL, applicationContext.getBean(ReversalTransactionProcessingTemplate.class));
    }

    @Bean
    Map<TransactionType, TransactionValidationStrategy> transactionValidationStrategyMap() {
        return Map.of(AUTHORIZE, applicationContext.getBean(AuthorizeTransactionValidationStrategy.class),
                CHARGE, applicationContext.getBean(ChargeTransactionValidationStrategy.class),
                REFUND, applicationContext.getBean(RefundTransactionValidationStrategy.class),
                REVERSAL, applicationContext.getBean(ReversalTransactionValidationStrategy.class));
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
                .allowedMethods(OPTIONS.name(), POST.name(), GET.name(), PUT.name(), PATCH.name(), DELETE.name(), HEAD.name())
                .allowedHeaders("Content-Type", "Origin", "X-Requested-With", "Accept", "Authorization",
                        "Access-Control-Allow-Methods", "Access-Control-Allow-Headers", "Access-Control-Allow-Origin");
    }
}
