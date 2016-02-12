package com.devicehive.application;

import com.devicehive.application.hazelcast.CuratorConfiguration;
import com.devicehive.application.kafka.KafkaConfig;
import com.devicehive.application.rabbit.RabbitConfig;
import com.devicehive.messages.common.IProducer;
import com.devicehive.messages.kafka.DefaultKafkaProducer;
import com.devicehive.messages.rabbit.DefaultRabbitProducer;
import io.swagger.jaxrs.config.BeanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
@ComponentScan(basePackages = { "com.devicehive" })
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EntityScan(basePackages = {"com.devicehive.model"})
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class DeviceHiveApplication extends SpringBootServletInitializer {

    public static final String MESSAGE_EXECUTOR = "DeviceHiveMessageService";

    @Autowired
    private Environment env;

    @Value("${message.broker:1}")
    private Integer messageBroker;

    public static void main(String... args) {
        SpringApplication.run(DeviceHiveApplication.class);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DeviceHiveApplication.class);
    }

    @Bean
    public Validator localValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Lazy(false)
    @Bean(name = MESSAGE_EXECUTOR)
    public ExecutorService messageExecutorService(@Value("${app.executor.size}") Integer executorSize) {
        return Executors.newFixedThreadPool(executorSize);
    }

    @Profile({"!test"})
    @Lazy(false)
    @Bean
    public IProducer provideMessageBroker() {
        switch (env.getActiveProfiles()[0]) {
            case "1":
                return new DefaultKafkaProducer();
            case "2":
                return new DefaultRabbitProducer();
            default:
                return new DefaultKafkaProducer();
        }
    }

    @Bean
    @Lazy(false)
    public BeanConfig swaggerConfig(@Value("${server.context-path}") String contextPath, @Value("${build.version}") String buildVersion) {
        String basePath = contextPath.equals("/") ? JerseyConfig.REST_PATH : contextPath + JerseyConfig.REST_PATH;
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Device Hive REST API");
        beanConfig.setVersion(buildVersion);
        beanConfig.setBasePath(basePath);
        beanConfig.setResourcePackage("com.devicehive.resource");
        beanConfig.setScan(true);
        return beanConfig;
    }
}
