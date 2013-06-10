package com.devicehive.websockets.json;


import com.devicehive.model.DeviceClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GsonFactory {

    public static Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateAdapter());
        builder.registerTypeAdapter(UUID.class, new UUIDAdapter());
        builder.setPrettyPrinting();
        return  builder.create();
    }
}
