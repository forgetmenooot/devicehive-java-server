package com.devicehive.application.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * Author: Y. Vovk
 * 15.02.16.
 */
@Configuration
@Profile("2")
public class RabbitSpringConfig {

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.virtual.host}")
    private String virtualHost;

    @Value("${rabbit.port}")
    private Integer port;

    @Value("${rabbit.concurrent.consumers:3}")
    private Integer concurrentConsumers;

    @Value("${rabbit.max.concurrent.consumers:9}")
    private Integer maxConcurrentConsumers;

    private static final String DEVICE_NOTIFICATION_QUEUE = "device_notification";
    private static final String COMMAND_NOTIFICATION_QUEUE = "command_notification";
    private static final String COMMAND_UPDATE_NOTIFICATION_QUEUE = "command_update_notification";
    private static final String DEVICE_NOTIFICATION_ZEPPELIN_QUEUE = "device_notification_zeppelin";

    private static final String DEVICE_NOTIFICATION_EXCHANGE = "devicehive_d_n";
    private static final String COMMAND_NOTIFICATION_EXCHANGE = "devicehive_c_n";
    private static final String COMMAND_UPDATE_NOTIFICATION_EXCHANGE = "devicehive_c_u_n";

    @Bean
    @Lazy(false)
    @Profile({"!test"})
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setPort(port);
        return factory;
    }

    @Bean
    @Lazy(false)
    @Profile({"!test"})
    public MessageConverter messageConverter() {
        return new JsonMessageConverter();
    }

    @Bean
    @Lazy(false)
    @Profile({"!test"})
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory());

        Queue deviceQueue = new Queue(DEVICE_NOTIFICATION_QUEUE, true, true, false);
        Queue commandQueue = new Queue(COMMAND_NOTIFICATION_QUEUE, true, true, false);
        Queue commandUpdateQueue = new Queue(COMMAND_UPDATE_NOTIFICATION_QUEUE, true, true, false);
        Queue deviceZepQueue = new Queue(DEVICE_NOTIFICATION_ZEPPELIN_QUEUE, true, false, false);

        FanoutExchange deviceExchange = new FanoutExchange(DEVICE_NOTIFICATION_EXCHANGE, true, false);
        FanoutExchange commandExchange = new FanoutExchange(COMMAND_NOTIFICATION_EXCHANGE, true, false);
        FanoutExchange commandUpdateExchange = new FanoutExchange(COMMAND_UPDATE_NOTIFICATION_EXCHANGE, true, false);

        admin.deleteQueue(DEVICE_NOTIFICATION_QUEUE);
        admin.deleteQueue(DEVICE_NOTIFICATION_ZEPPELIN_QUEUE);
        admin.deleteQueue(COMMAND_NOTIFICATION_QUEUE);
        admin.deleteQueue(COMMAND_UPDATE_NOTIFICATION_QUEUE);

        admin.removeBinding(BindingBuilder.bind(deviceQueue).to(deviceExchange));
        admin.removeBinding(BindingBuilder.bind(commandQueue).to(commandExchange));
        admin.removeBinding(BindingBuilder.bind(commandUpdateQueue).to(commandUpdateExchange));
        admin.removeBinding(BindingBuilder.bind(deviceZepQueue).to(deviceExchange));

        admin.deleteExchange(DEVICE_NOTIFICATION_EXCHANGE);
        admin.deleteExchange(COMMAND_NOTIFICATION_EXCHANGE);
        admin.deleteExchange(COMMAND_UPDATE_NOTIFICATION_EXCHANGE);

        admin.declareQueue(deviceQueue);
        admin.declareQueue(commandQueue);
        admin.declareQueue(commandUpdateQueue);
        admin.declareQueue(deviceZepQueue);

        admin.declareExchange(deviceExchange);
        admin.declareExchange(commandExchange);
        admin.declareExchange(commandUpdateExchange);

        admin.declareBinding(BindingBuilder.bind(deviceQueue).to(deviceExchange));
        admin.declareBinding(BindingBuilder.bind(commandQueue).to(commandExchange));
        admin.declareBinding(BindingBuilder.bind(commandUpdateQueue).to(commandUpdateExchange));
        admin.declareBinding(BindingBuilder.bind(deviceZepQueue).to(deviceExchange));

        return admin;
    }

    @Bean
    @Lazy(false)
    @Profile({"!test"})
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

    @Bean
    @Lazy(false)
    @Profile({"!test"})
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        return factory;
    }

}
