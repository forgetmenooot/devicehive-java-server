package com.devicehive.application.rabbit;

import com.devicehive.messages.common.CommandConsumer;
import com.devicehive.messages.common.CommandUpdateConsumer;
import com.devicehive.messages.common.IConsumer;
import com.devicehive.messages.common.NotificationConsumer;
import com.devicehive.messages.kafka.*;
import com.devicehive.messages.rabbit.*;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
@Configuration
public class RabbitConfig {

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

    @Value("${rabbit.recovery.enabled}")
    private Boolean recoveryEnabled;

    @Value("${rabbit.recovery.interval:10000}")
    private Integer recoverInterval;

    @Autowired
    private EntityToByteConverter<DeviceCommand> deviceCommandEntityToByteConverter;

    @Autowired
    private EntityToByteConverter<DeviceNotification> deviceNotificationEntityToByteConverter;

    @Bean
    @Lazy(false)
    public EntityToByteConverter provideConverter() {
        return new EntityToByteConverter();
    }

    @Bean(name="commandUpdateNotificationConsumer")
    @Lazy(false)
    public IConsumer provideCommandUpdateConsumer() {
        return new CommandUpdateConsumer();
    }

    @Bean(name="commandNotificationConsumer")
    @Lazy(false)
    public IConsumer provideCommandConsumer() {
        return new CommandConsumer();
    }

    @Bean(name="deviceNotificationConsumer")
    @Lazy(false)
    public IConsumer provideDeviceConsumer() {
        return new NotificationConsumer();
    }

    private Connection provideRabbitConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setHost(host);
        factory.setPort(port);
        factory.setAutomaticRecoveryEnabled(recoveryEnabled);
        if (recoveryEnabled) {
            factory.setNetworkRecoveryInterval(recoverInterval);
        }
        return factory.newConnection();
    }

    @Profile({"!test"})
    @Bean(name = "rabbitDeviceNotificationProducer", destroyMethod = "close")
    @Lazy(false)
    public Channel provideDeviceNotProducer() throws IOException, TimeoutException {
        return provideCommonConfig("device_notification", "device_n");
    }

    @Profile({"!test"})
    @Bean(name = "rabbitCommandUpdateNotificationProducer", destroyMethod = "close")
    @Lazy(false)
    public Channel provideCommandUpdateNotProducer() throws IOException, TimeoutException {
        return provideCommonConfig("command_update_notification", "command_u_n");
    }

    @Profile({"!test"})
    @Bean(name = "rabbitCommandNotificationProducer", destroyMethod = "close")
    @Lazy(false)
    public Channel provideCommandNotProducer() throws IOException, TimeoutException {
        return provideCommonConfig("command_notification", "command_n");
    }

    @Profile({"!test"})
    @Bean(name = "rabbitDeviceNotificationConsumer", destroyMethod = "close")
    @Lazy(false)
    public Channel provideDeviceNotConsumer() throws IOException, TimeoutException {
        Channel channel = provideCommonConfig("device_notification", "device_n");
        provideCommonConsumerConfig(channel, "device_notification", provideDeviceConsumer(), deviceNotificationEntityToByteConverter);
        return channel;
    }

    @Profile({"!test"})
    @Bean(name = "rabbitCommandUpdateNotificationConsumer", destroyMethod = "close")
    @Lazy(false)
    public Channel provideCommandUpdateNotConsumer() throws IOException, TimeoutException {
        Channel channel = provideCommonConfig("command_update_notification", "command_u_n");
        provideCommonConsumerConfig(channel, "command_update_notification", provideCommandUpdateConsumer(), deviceCommandEntityToByteConverter);
        return channel;
    }

    @Profile({"!test"})
    @Bean(name = "rabbitCommandNotificationConsumer", destroyMethod = "close")
    @Lazy(false)
    public Channel provideCommandNotConsumer() throws IOException, TimeoutException {
        Channel channel = provideCommonConfig("command_notification", "command_n");
        provideCommonConsumerConfig(channel, "command_notification", provideCommandUpdateConsumer(), deviceCommandEntityToByteConverter);
        return channel;
    }

    private <T> void provideCommonConsumerConfig(Channel channel, String queue, IConsumer consumer, EntityToByteConverter<T> converter) throws IOException {
        Consumer defaultConsumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                T message = converter.fromBytes(body);

                try {
                    consumer.submitMessage(message);
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(queue, false, defaultConsumer);
    }

    private Channel provideCommonConfig(String queue, String routingKey)
            throws IOException, TimeoutException {
        Channel channel = provideRabbitConnection().createChannel();
        channel.exchangeDeclare("devicehive", "direct", true);
        channel.queueDeclare(queue, true, false, false, null);
        channel.queueBind(queue, "devicehive", routingKey);
//        channel.basicQos(prefetchCount=1);//don't dispatch a new message to a worker until it has processed and acknowledged the previous one
        return channel;
    }

}
