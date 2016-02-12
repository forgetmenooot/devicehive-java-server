package com.devicehive.messages.common;

import com.devicehive.model.DeviceCommand;
import org.springframework.stereotype.Component;

/**
 * Author: Y. Vovk
 * 12.02.16.
 */
@Component
public class JsonCommandNotificationConverter extends JsonConverter<DeviceCommand> {

    public JsonCommandNotificationConverter() {
        super(DeviceCommand.class);
    }
}
