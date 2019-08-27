package com.rulerbug.bt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothService extends Service {

    private BluetoothDevice remoteDevice;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String MAC_KEY = "MAC_KEY";
    public static String CLOSE_BLUETOOTH = "CLOSE_BLUETOOTH";
    private BluetoothAdapter bluetoothAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CLOSE_BLUETOOTH);
        registerReceiver(closeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (closeReceiver != null) {
            unregisterReceiver(closeReceiver);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String mac = intent.getStringExtra(MAC_KEY);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        remoteDevice = bluetoothAdapter.getRemoteDevice(mac);
        bluetoothAdapter.cancelDiscovery();
        connection(remoteDevice);
        return super.onStartCommand(intent, flags, startId);
    }

    private void connection(final BluetoothDevice remoteDevice) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    final BluetoothSocket socket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket.connect();
                                InputStream is = socket.getInputStream();
                                while (is != null) {
                                    int count = is.available();
                                    int readCount = 0;
                                    byte[] bytes = new byte[1024];
                                    while (readCount < count) {
                                        readCount += is.read(bytes, readCount, count - readCount);
                                    }
                                    String btMsg = BluetoothUtils.BytesToString(bytes, 0, readCount);
                                    sendMsg(btMsg);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void sendMsg(String btMsg) {
        Log.e("1123", btMsg);
    }

    public BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothGatt bluetoothGatt = remoteDevice.connectGatt(context, true, new BluetoothGattCallback() {
            });
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    };
}
