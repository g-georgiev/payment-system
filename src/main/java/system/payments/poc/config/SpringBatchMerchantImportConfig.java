package system.payments.poc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import system.payments.poc.model.Merchant;
import system.payments.poc.processor.MerchantProcessor;
import system.payments.poc.repository.MerchantRepository;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class SpringBatchMerchantImportConfig {

    private final MerchantRepository merchantRepository;

    private static final String IMPORT_CSV_LOCATION = "static/import/merchant";

    //Item Reader
    @Bean
    @StepScope
    public MultiResourceItemReader<Merchant> multiResourceReader() {
        return new MultiResourceItemReaderBuilder<Merchant>()
                .name("csvMerchantDirReader")
                .delegate(flatFileItemReader())
                .resources(resources())
                .build();
    }

    private Resource[] resources() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            return resolver.getResources(IMPORT_CSV_LOCATION + "/*.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FlatFileItemReader<Merchant> flatFileItemReader() {
        FlatFileItemReader<Merchant> itemReader = new FlatFileItemReader<>();
        itemReader.setName("csvMerchantReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Merchant> lineMapper() {
        DefaultLineMapper<Merchant> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("username", "password", "name", "email", "description");

        BeanWrapperFieldSetMapper<Merchant> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Merchant.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    //Item Processor
    @Bean
    public MerchantProcessor processor() {
        return new MerchantProcessor(merchantRepository);
    }

    //Item Writer
    @Bean
    public RepositoryItemWriter<Merchant> writer() {
        RepositoryItemWriter<Merchant> writer = new RepositoryItemWriter<>();
        writer.setRepository(merchantRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step", jobRepository).<Merchant, Merchant>chunk(10, transactionManager)
                .reader(multiResourceReader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importMerchants: " + LocalDate.now(), jobRepository)
                .flow(step1(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor jobRegistryBeanPostProcessorRemover() {
        return registry -> registry.removeBeanDefinition("jobRegistryBeanPostProcessor");
    }
}