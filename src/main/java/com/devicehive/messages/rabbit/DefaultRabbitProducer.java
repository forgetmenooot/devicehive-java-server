package com.devicehive.messages.rabbit;

import com.devicehive.messages.common.IProducer;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
public class DefaultRabbitProducer implements IProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRabbitProducer.class);

    @Autowired
    @Qualifier("rabbitDeviceNotificationProducer")
    private Channel notificationProducer;

    @Autowired
    @Qualifier("rabbitCommandNotificationProducer")
    private Channel commandProducer;

    @Autowired
    @Qualifier("rabbitCommandUpdateNotificationProducer")
    private Channel commandUpdateProducer;

    @Autowired
    private EntityToByteConverter<DeviceCommand> deviceCommandEntityToByteConverter;

    @Autowired
    private EntityToByteConverter<DeviceNotification> deviceNotificationEntityToByteConverter;

    @Override
    public void produceDeviceNotificationMsg(DeviceNotification message) {
        try {
            notificationProducer.basicPublish("devicehive", "device_n", MessageProperties.PERSISTENT_TEXT_PLAIN, deviceNotificationEntityToByteConverter.toBytes(message));
        } catch (IOException e) {
            LOGGER.error("Unable to publish device notification due to encoding to bytes", e);
        }
    }

    @Override
    public void produceDeviceCommandMsg(DeviceCommand message) {
        try {
            commandProducer.basicPublish("devicehive", "command_n", MessageProperties.PERSISTENT_TEXT_PLAIN, deviceCommandEntityToByteConverter.toBytes(message));
        } catch (IOException e) {
            LOGGER.error("Unable to publish command notification due to encoding to bytes", e);
        }
    }

    @Override
    public void produceDeviceCommandUpdateMsg(DeviceCommand message) {
        try {
            commandUpdateProducer.basicPublish("devicehive", "command_u_n", MessageProperties.PERSISTENT_TEXT_PLAIN, deviceCommandEntityToByteConverter.toBytes(message));
        } catch (IOException e) {
            LOGGER.error("Unable to publish command update notification due to encoding to bytes", e);
        }
    }
}
