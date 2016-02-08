package com.devicehive.messages.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Author: Y. Vovk
 * 05.02.16.
 */
public class EntityToByteConverter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityToByteConverter.class);

    public byte[] toBytes(T obj) {
        byte[] bytes = new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush();
            oos.reset();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Unable to write to output stream", e);
        }
        return bytes;
    }

    public T fromBytes(byte[] body) {
        T obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(body);
             ObjectInputStream ois = new ObjectInputStream(bis); ){
            obj = (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to read to output stream", e);
        }
        return obj;
    }
}
