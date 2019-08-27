package com.rulerbug.bt;

import android.app.Application;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.net.Socket;

public class mApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Socket getSocket() {
        return null;
    }
}
