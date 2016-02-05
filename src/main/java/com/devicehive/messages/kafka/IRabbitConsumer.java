package com.devicehive.messages.kafka;

import com.devicehive.model.DeviceCommand;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
public interface IRabbitConsumer<T> {

    void submitMessage(T message);
}
