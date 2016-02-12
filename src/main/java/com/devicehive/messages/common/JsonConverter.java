package com.devicehive.messages.common;

import com.devicehive.json.adapters.TimestampAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Author: Y. Vovk
 * 12.02.16.
 */
public class JsonConverter<T> {

    private Gson gson;
    private Class<T> type;

    public JsonConverter(Class<T> type) {
        gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(Date.class, new TimestampAdapter()).create();
        this.type = type;
    }

    public byte[] toBytes(T notification) {
        return toJsonString(notification).getBytes();
    }

    public String toJsonString(T notification) {
        return gson.toJson(notification);
    }

    public T fromBytes(byte[] bytes) {
        try {
            return gson.fromJson(new String(bytes, "UTF-8"), type);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Byte array cannot be converted to object", e);
        }
    }

    public T fromString(String string) {
        return gson.fromJson(string, type);
    }

}
