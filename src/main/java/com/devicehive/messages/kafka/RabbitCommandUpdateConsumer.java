package com.devicehive.messages.kafka;

import com.devicehive.application.DeviceHiveApplication;
import com.devicehive.messages.subscriptions.CommandUpdateSubscription;
import com.devicehive.messages.subscriptions.SubscriptionManager;
import com.devicehive.model.DeviceCommand;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
public class RabbitCommandUpdateConsumer implements IRabbitConsumer<DeviceCommand>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitCommandUpdateConsumer.class);

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    @Qualifier(DeviceHiveApplication.MESSAGE_EXECUTOR)
    private ExecutorService mes;

    public void submitMessage(DeviceCommand message) {
        LOGGER.debug("Device command update was submitted: {}", message.getId());

        Set<CommandUpdateSubscription> subs = subscriptionManager.getCommandUpdateSubscriptionStorage()
                .getByCommandId(message.getId());
        for (CommandUpdateSubscription commandUpdateSubscription : subs) {
            mes.submit(commandUpdateSubscription.getHandlerCreator()
                    .getHandler(message, commandUpdateSubscription.getSubscriptionId()));
        }
    }

}
