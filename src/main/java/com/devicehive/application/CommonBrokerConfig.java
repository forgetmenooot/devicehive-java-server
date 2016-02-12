package com.devicehive.application;

import com.devicehive.messages.common.CommandConsumer;
import com.devicehive.messages.common.CommandUpdateConsumer;
import com.devicehive.messages.common.IConsumer;
import com.devicehive.messages.common.NotificationConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Author: Y. Vovk
 * 11.02.16.
 */
@Configuration
public class CommonBrokerConfig {

    @Bean(name="commandUpdateNotificationConsumer")
    @Lazy(false)
    public IConsumer provideCommandUpdateConsumer() {
        return new CommandUpdateConsumer();
    }

    @Bean(name="commandNotificationConsumer")
    @Lazy(false)
    public IConsumer provideCommandConsumer() {
        return new CommandConsumer();
    }

    @Bean(name="deviceNotificationConsumer")
    @Lazy(false)
    public IConsumer provideDeviceConsumer() {
        return new NotificationConsumer();
    }
}
