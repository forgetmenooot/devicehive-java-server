package com.devicehive.messages.rabbit;

import com.devicehive.messages.common.IProducer;
import com.devicehive.messages.common.JsonCommandNotificationConverter;
import com.devicehive.messages.common.JsonDeviceNotificationConverter;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Y. Vovk
 * 15.02.16.
 */
public class SpringRabbitProducer implements IProducer {

    @Autowired
    private JsonDeviceNotificationConverter deviceNotificationConverter;

    @Autowired
    private JsonCommandNotificationConverter commandNotificationConverter;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void produceDeviceNotificationMsg(DeviceNotification message) {
        rabbitTemplate.convertAndSend("devicehive_d_n", "", deviceNotificationConverter.toBytes(message));
    }

    @Override
    public void produceDeviceCommandMsg(DeviceCommand message) {
        rabbitTemplate.convertAndSend("devicehive_c_n", "", commandNotificationConverter.toBytes(message));

    }

    @Override
    public void produceDeviceCommandUpdateMsg(DeviceCommand message) {
        rabbitTemplate.convertAndSend("devicehive_c_u_n",  "", commandNotificationConverter.toBytes(message));
    }
}

