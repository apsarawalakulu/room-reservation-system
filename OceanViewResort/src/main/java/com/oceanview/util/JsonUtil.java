package com.oceanview.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    public static Gson getGson() {
        return gson;
    }
    
    public static String toJson(Object object) {
        return gson.toJson(object);
    }
    
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
