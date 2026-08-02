package com.customandroid.server;

public class SystemServer {
    public void startBootstrapServices() {
        System.out.println("CustomAndroid system services started");
    }

    public static void main(String[] args) {
        new SystemServer().startBootstrapServices();
    }
}
