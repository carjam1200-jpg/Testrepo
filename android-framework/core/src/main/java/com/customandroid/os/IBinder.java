package com.customandroid.os;

public interface IBinder {
    Object transact(int code, Object data);
}
