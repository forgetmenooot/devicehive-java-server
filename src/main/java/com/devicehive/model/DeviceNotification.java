package com.devicehive.model;


import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * TODO JavaDoc
 */
public class DeviceNotification {

    @SerializedName("id")
    private Integer id;

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("notification")
    private String notification;

    @SerializedName("parameters")
    private JsonElement parameters;

    public DeviceNotification() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public JsonElement getParameters() {
        return parameters;
    }

    public void setParameters(JsonElement parameters) {
        this.parameters = parameters;
    }
}
