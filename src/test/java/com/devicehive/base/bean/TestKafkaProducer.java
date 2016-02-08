package com.devicehive.base.bean;

import com.devicehive.application.DeviceHiveApplication;
import com.devicehive.messages.common.IProducer;
import com.devicehive.messages.kafka.CommandKafkaConsumer;
import com.devicehive.messages.kafka.CommandUpdateKafkaConsumer;
import com.devicehive.messages.kafka.NotificationKafkaConsumer;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class TestKafkaProducer implements IProducer {

    @Autowired
    private NotificationKafkaConsumer notificationConsumer;

    @Autowired
    private CommandKafkaConsumer commandConsumer;

    @Autowired
    private CommandUpdateKafkaConsumer commandUpdateConsumer;

    @Autowired
    @Qualifier(DeviceHiveApplication.MESSAGE_EXECUTOR)
    private ExecutorService executorService;

    @Override
    public void produceDeviceNotificationMsg(DeviceNotification message) {
        executorService.submit(() -> notificationConsumer.submitMessage(message));
    }

    @Override
    public void produceDeviceCommandMsg(DeviceCommand message) {
        executorService.submit(() -> commandConsumer.submitMessage(message));
    }

    @Override
    public void produceDeviceCommandUpdateMsg(DeviceCommand message) {
        executorService.submit(() -> commandUpdateConsumer.submitMessage(message));
    }
}
