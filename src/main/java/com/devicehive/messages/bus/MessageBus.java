package com.devicehive.messages.bus;

import com.devicehive.messages.common.IProducer;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import com.devicehive.model.HazelcastEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by tmatvienko on 12/30/14.
 */
@Component
@Lazy(false)
public class MessageBus {
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessageBus.class);

    @Autowired
    private IProducer producer;

    public <T extends HazelcastEntity> void publish(T hzEntity) {
        if (hzEntity instanceof DeviceNotification) {
            producer.produceDeviceNotificationMsg((DeviceNotification) hzEntity);
        } else if (hzEntity instanceof DeviceCommand) {
            DeviceCommand command = (DeviceCommand) hzEntity;
            if (command.getIsUpdated()) {
                producer.produceDeviceCommandUpdateMsg(command);
            } else {
                producer.produceDeviceCommandMsg(command);
            }
        } else {
            final String msg = String.format("Unsupported hazelcast entity class: %s", hzEntity.getClass());
            LOGGER.warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
