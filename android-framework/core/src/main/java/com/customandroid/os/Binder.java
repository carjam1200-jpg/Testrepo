package com.customandroid.os;

public class Binder implements IBinder {
    @Override
    public Object transact(int code, Object data) {
        return onTransact(code, data);
    }

    protected Object onTransact(int code, Object data) {
        return null;
    }
}
