package com.hdytyldrm.batterylevel.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hdytyldrm.batterylevel.service.UnifiedBluetoothService;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            Log.d(TAG, "📶 Bluetooth state changed: " + getStateString(state));

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "📴 Bluetooth turned off - stopping service");
                    stopBluetoothService(context);
                    break;

                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "📶 Bluetooth turned on - starting service");
                    startBluetoothService(context);
                    break;

                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "📴 Bluetooth turning off...");
                    break;

                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(TAG, "📶 Bluetooth turning on...");
                    break;
            }
        }
    }

    private void startBluetoothService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, UnifiedBluetoothService.class);

            // Modern Android versiyonları için daha güvenli başlatma yöntemi olan
            // ContextCompat kullanımı daha iyidir.
            androidx.core.content.ContextCompat.startForegroundService(context, serviceIntent);

            Log.d(TAG, "✅ Bluetooth service start requested via ContextCompat");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting bluetooth service", e);
        }
    }

    private void stopBluetoothService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, UnifiedBluetoothService.class);
            context.stopService(serviceIntent);
            Log.d(TAG, "⏹️ Bluetooth service stopped");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error stopping bluetooth service", e);
        }
    }

    private String getStateString(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF: return "OFF";
            case BluetoothAdapter.STATE_ON: return "ON";
            case BluetoothAdapter.STATE_TURNING_OFF: return "TURNING_OFF";
            case BluetoothAdapter.STATE_TURNING_ON: return "TURNING_ON";
            default: return "UNKNOWN";
        }
    }
}