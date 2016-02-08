package com.devicehive.messages.common;

import com.devicehive.application.DeviceHiveApplication;
import com.devicehive.messages.kafka.AbstractKafkaConsumer;
import com.devicehive.messages.subscriptions.CommandUpdateSubscription;
import com.devicehive.messages.subscriptions.SubscriptionManager;
import com.devicehive.model.DeviceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Author: Y. Vovk
 * 08.02.16.
 */
public class CommandUpdateConsumer extends AbstractKafkaConsumer<DeviceCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandUpdateConsumer.class);

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    @Qualifier(DeviceHiveApplication.MESSAGE_EXECUTOR)
    private ExecutorService mes;

    @Override
    public void submitMessage(final DeviceCommand message) {
        LOGGER.debug("Device command update was submitted: {}", message.getId());

        Set<CommandUpdateSubscription> subs = subscriptionManager.getCommandUpdateSubscriptionStorage()
                .getByCommandId(message.getId());
        for (CommandUpdateSubscription commandUpdateSubscription : subs) {
            mes.submit(commandUpdateSubscription.getHandlerCreator()
                    .getHandler(message, commandUpdateSubscription.getSubscriptionId()));
        }
    }
}
