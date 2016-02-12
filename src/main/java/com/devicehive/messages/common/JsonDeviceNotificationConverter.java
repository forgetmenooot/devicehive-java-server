package com.devicehive.messages.common;

import com.devicehive.model.DeviceNotification;
import org.springframework.stereotype.Component;

/**
 * Author: Y. Vovk
 * 12.02.16.
 */
@Component
public class JsonDeviceNotificationConverter extends JsonConverter<DeviceNotification> {

    public JsonDeviceNotificationConverter() {
        super(DeviceNotification.class);
    }
}
