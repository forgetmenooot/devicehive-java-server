package com.devicehive.messages.kafka;

import com.devicehive.application.kafka.KafkaConfig;
import com.devicehive.configuration.Constants;
import com.devicehive.messages.common.IProducer;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by tmatvienko on 12/24/14.
 */
public class DefaultKafkaProducer implements IProducer {

    @Autowired
    @Qualifier(KafkaConfig.NOTIFICATION_PRODUCER)
    private Producer<String, DeviceNotification> notificationProducer;

    @Autowired
    @Qualifier(KafkaConfig.COMMAND_PRODUCER)
    private Producer<String, DeviceCommand> commandProducer;

    @Override
    public void produceDeviceNotificationMsg(DeviceNotification message) {
        notificationProducer.send(new KeyedMessage<>(Constants.NOTIFICATION_TOPIC_NAME, message.getDeviceGuid(), message));
    }

    @Override
    public void produceDeviceCommandMsg(DeviceCommand message) {
        commandProducer.send(new KeyedMessage<>(Constants.COMMAND_TOPIC_NAME, message.getDeviceGuid(), message));
    }

    @Override
    public void produceDeviceCommandUpdateMsg(DeviceCommand message) {
        commandProducer.send(new KeyedMessage<>(Constants.COMMAND_UPDATE_TOPIC_NAME, message.getDeviceGuid(), message));
    }

}
