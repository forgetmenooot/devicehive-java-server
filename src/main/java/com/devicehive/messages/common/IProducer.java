package com.devicehive.messages.common;

import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;

/**
 * Author: Y. Vovk
 * 08.02.16.
 */
public interface IProducer {

    void produceDeviceNotificationMsg(DeviceNotification message);

    void produceDeviceCommandMsg(DeviceCommand message);

    void produceDeviceCommandUpdateMsg(DeviceCommand message);

}
