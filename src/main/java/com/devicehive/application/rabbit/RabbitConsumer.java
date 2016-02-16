package com.devicehive.application.rabbit;

import com.devicehive.messages.common.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Author: Y. Vovk
 * 15.02.16.
 */
@Component
@Profile({"!test"})
public class RabbitConsumer {

    @Autowired
    @Qualifier("commandUpdateNotificationConsumer")
    private IConsumer commandUpdateConsumer;

    @Autowired
    @Qualifier("commandNotificationConsumer")
    public IConsumer commandConsumer;

    @Autowired
    @Qualifier("deviceNotificationConsumer")
    public IConsumer deviceConsumer;

    @Autowired
    private JsonDeviceNotificationConverter deviceNotificationConverter;

    @Autowired
    private JsonCommandNotificationConverter commandNotificationConverter;

    @RabbitListener(queues = "command_notification")
    public void processCommandNotifications(Message data) {
        commandConsumer.submitMessage(commandNotificationConverter.fromBytes(data.getBody()));
    }

    @RabbitListener(queues = "device_notification")
    public void processDeviceNotifications(Message data) {
        deviceConsumer.submitMessage(deviceNotificationConverter.fromBytes(data.getBody()));
    }

    @RabbitListener(queues = "command_update_notification")
    public void processCommandUpdateNotifications(Message data) {
        commandUpdateConsumer.submitMessage(commandNotificationConverter.fromBytes(data.getBody()));
    }

}
