package com.devicehive.messages.common;

/**
 * Author: Y. Vovk
 * 08.02.16.
 */
public interface IConsumer<T> {

    void submitMessage(T message);
}
