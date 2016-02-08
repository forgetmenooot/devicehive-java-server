package com.devicehive.messages.kafka;

import com.devicehive.messages.common.IConsumer;
import com.devicehive.model.DeviceNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by tmatvienko on 12/24/14.
 */
public class NotificationKafkaConsumer extends AbstractKafkaConsumer<DeviceNotification> {

    @Autowired
    @Qualifier("deviceNotificationConsumer")
    private IConsumer<DeviceNotification> consumer;

    @Override
    public void submitMessage(final DeviceNotification message) {
        consumer.submitMessage(message);
    }
}
