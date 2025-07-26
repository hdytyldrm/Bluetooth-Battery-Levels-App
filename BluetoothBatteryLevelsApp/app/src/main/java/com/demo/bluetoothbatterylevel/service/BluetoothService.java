package com.demo.bluetoothbatterylevel.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import com.demo.bluetoothbatterylevel.R;
import com.demo.bluetoothbatterylevel.activity.StartActivity;
import com.demo.bluetoothbatterylevel.utils.Common;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothService extends Service {
    public static Notification.Builder mBuilder;
    public static NotificationManager notification;
    public static RemoteViews remoteViews;
    String TAG = "PlayMusicService";
    BluetoothAdapter bAdapter;
    public BluetoothDevice connectDevice;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Log.d(BluetoothService.this.TAG, "onReceive: ");
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: ACTION_FOUND");
            } else if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: ACTION_ACL_CONNECTED");
                BluetoothService.remoteViews.setViewVisibility(R.id.disConnectRl, 8);
                BluetoothService.remoteViews.setViewVisibility(R.id.connectRl, 0);
                BluetoothService.remoteViews.setTextViewText(R.id.name, bluetoothDevice.getName());
                BluetoothService.notification.notify(115600, BluetoothService.mBuilder.build());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothAdapter.getDefaultAdapter().getProfileProxy(BluetoothService.this, BluetoothService.this.serviceListener, 1);
                    }
                }, 1000);
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: ACTION_DISCOVERY_FINISHED");
            } else if ("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED".equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: ACTION_ACL_DISCONNECT_REQUESTED");
            } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: ACTION_ACL_DISCONNECTED");
                BluetoothService.remoteViews.setViewVisibility(R.id.connectRl, 8);
                BluetoothService.remoteViews.setViewVisibility(R.id.disConnectRl, 0);
                BluetoothService.notification.notify(115600, BluetoothService.mBuilder.build());
            } else if (Common.ACTION_BATTERY_LEVEL_CHANGED.equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: ACTION_BATTERY_LEVEL_CHANGED");
            } else if (Common.EXTRA_BATTERY_LEVEL.equals(action)) {
                Log.d(BluetoothService.this.TAG, "onReceive: EXTRA_BATTERY_LEVEL");
            }
        }
    };
    BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int i) {
        }

        @Override
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            for (BluetoothDevice next : bluetoothProfile.getConnectedDevices()) {
                if (next.getBluetoothClass().getDeviceClass() == 1028 || next.getBluetoothClass().getDeviceClass() == 1048) {
                    try {
                        Method method = next.getClass().getMethod("getBatteryLevel", new Class[0]);
                        if (method != null) {
                            float intValue = (float) ((Integer) method.invoke(next, new Object[0])).intValue();
                            int i2 = (int) intValue;
                            Log.d(BluetoothService.this.TAG, "onServiceConnected: headphones connected with charge percentage " + intValue);
                            BluetoothService.remoteViews.setTextViewText(R.id.battery, i2 + "%");
                            BluetoothService.remoteViews.setProgressBar(R.id.progressBar, 100, i2, false);
                            BluetoothService.notification.notify(115600, BluetoothService.mBuilder.build());
                        } else {
                            BluetoothService.remoteViews.setTextViewText(R.id.battery, "No");
                            BluetoothService.remoteViews.setProgressBar(R.id.progressBar, 100, 0, false);
                            BluetoothService.notification.notify(115600, BluetoothService.mBuilder.build());
                        }
                    } catch (IllegalAccessException | NoSuchMethodException | NullPointerException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(i, bluetoothProfile);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.bAdapter = BluetoothAdapter.getDefaultAdapter();
        notification = (NotificationManager) getSystemService("notification");
        remoteViews = new RemoteViews(getPackageName(), R.layout.service_layout);
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra("OpenActivity", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("channelid", "channelname", 3);
            notificationChannel.setLightColor(-16776961);
            notificationChannel.setShowBadge(false);
            notification.createNotificationChannel(notificationChannel);
            mBuilder = new Notification.Builder(this, "channelid");
        } else {
            mBuilder = new Notification.Builder(this);
        }
        BluetoothAdapter bluetoothAdapter = this.bAdapter;
        if (bluetoothAdapter != null) {
            BluetoothDevice connectDevice2 = getConnectDevice(bluetoothAdapter.getBondedDevices());
            this.connectDevice = connectDevice2;
            if (connectDevice2 == null) {
                remoteViews.setViewVisibility(R.id.connectRl, 8);
                remoteViews.setViewVisibility(R.id.disConnectRl, 0);
            } else {
                remoteViews.setViewVisibility(R.id.disConnectRl, 8);
                remoteViews.setViewVisibility(R.id.connectRl, 0);
                remoteViews.setTextViewText(R.id.name, this.connectDevice.getName());
                BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, this.serviceListener, 1);
            }
        } else {
            remoteViews.setViewVisibility(R.id.connectRl, 8);
            remoteViews.setViewVisibility(R.id.disConnectRl, 0);
        }
        mBuilder.setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(false).setOngoing(true).setShowWhen(true).setContentIntent(activity).setCustomBigContentView(remoteViews).setTicker(getResources().getString(R.string.app_name)).setCustomContentView(remoteViews);
        startForeground(115600, mBuilder.build());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        //registerReceiver(this.receiver, intentFilter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(this.receiver, intentFilter, RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(this.receiver, intentFilter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    public BluetoothDevice getConnectDevice(Set<BluetoothDevice> set) {
        if (set.size() <= 0) {
            return null;
        }
        for (BluetoothDevice next : set) {
            if (isConnected(next)) {
                return next;
            }
        }
        return null;
    }

    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        try {
            Class[] clsArr = null;
            Object[] objArr = null;
            return ((Boolean) bluetoothDevice.getClass().getMethod("isConnected", (Class[]) null).invoke(bluetoothDevice, (Object[]) null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }
}
