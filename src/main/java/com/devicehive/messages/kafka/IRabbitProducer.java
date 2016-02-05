package com.devicehive.messages.kafka;

import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;

import java.io.IOException;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
public interface IRabbitProducer {

    void produceDeviceNotificationMsg(DeviceNotification message);

    void produceDeviceCommandMsg(DeviceCommand message);

    void produceDeviceCommandUpdateMsg(DeviceCommand message);

}
