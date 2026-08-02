package com.customandroid.os;

import java.util.HashMap;
import java.util.Map;

public final class ServiceManager {
    private static final Map<String, IBinder> SERVICES = new HashMap<>();

    public static void addService(String name, IBinder service) {
        SERVICES.put(name, service);
    }

    public static IBinder getService(String name) {
        return SERVICES.get(name);
    }

    private ServiceManager() {}
}
