package com.devicehive.messages.kafka;

import com.devicehive.application.DeviceHiveApplication;
import com.devicehive.configuration.Constants;
import com.devicehive.messages.subscriptions.NotificationSubscription;
import com.devicehive.messages.subscriptions.SubscriptionManager;
import com.devicehive.model.DeviceNotification;
import com.devicehive.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
public class RabbitNotificationConsumer implements IRabbitConsumer<DeviceNotification> {

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    @Qualifier(DeviceHiveApplication.MESSAGE_EXECUTOR)
    private ExecutorService mes;

    @Override
    public void submitMessage(final DeviceNotification message) {
        Set<UUID> subscribersIds = new HashSet<>();
        Set<NotificationSubscription> subs =
                subscriptionManager.getNotificationSubscriptionStorage().getByDeviceGuid(
                        message.getDeviceGuid());
        for (NotificationSubscription subscription : subs) {
            if (subscription.getNotificationNames() != null
                    && !subscription.getNotificationNames().contains(message.getNotification())) {
                continue;
            }
            boolean hasAccess = deviceService.hasAccessTo(subscription.getPrincipal(), message.getDeviceGuid());
            if (hasAccess) {
                mes.submit(
                        subscription.getHandlerCreator().getHandler(message, subscription.getSubscriptionId()));
            }
            subscribersIds.add(subscription.getSubscriptionId());
        }

        Set<NotificationSubscription> subsForAll = (subscriptionManager.getNotificationSubscriptionStorage()
                .getByDeviceGuid(Constants.NULL_SUBSTITUTE));

        for (NotificationSubscription subscription : subsForAll) {
            if (subscription.getNotificationNames() != null
                    && !subscription.getNotificationNames().contains(message.getNotification())) {
                continue;
            }
            if (!subscribersIds.contains(subscription.getSubscriptionId())) {
                boolean
                        hasAccess =
                        deviceService.hasAccessTo(subscription.getPrincipal(), message.getDeviceGuid());
                if (hasAccess) {
                    mes.submit(subscription.getHandlerCreator()
                            .getHandler(message, subscription.getSubscriptionId()));
                }
            }
        }
    }
}
