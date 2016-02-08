package com.devicehive.messages.kafka;

import com.devicehive.messages.common.IConsumer;
import com.devicehive.model.DeviceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by tmatvienko on 1/30/15.
 */
public class CommandUpdateKafkaConsumer extends AbstractKafkaConsumer<DeviceCommand> {

    @Autowired
    @Qualifier("commandUpdateNotificationConsumer")
    private IConsumer<DeviceCommand> consumer;

    @Override
    public void submitMessage(final DeviceCommand message) {
        consumer.submitMessage(message);
    }
}
