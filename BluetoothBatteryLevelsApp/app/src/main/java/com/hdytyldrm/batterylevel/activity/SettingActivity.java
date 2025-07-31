package com.hdytyldrm.batterylevel.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.ads.AdsGeneral;
import com.hdytyldrm.batterylevel.audio.BluetoothAudioVolumeObserver;
import com.hdytyldrm.batterylevel.databinding.ActivitySettingBinding;
import com.hdytyldrm.batterylevel.utils.SpManager;

import java.lang.reflect.Method;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";

    private ActivitySettingBinding binding;
    private AudioManager audioManager;
    private BluetoothAdapter bAdapter;
    private BluetoothDevice connectedAppleDevice;
    private BluetoothAudioVolumeObserver btAudioVolumeObserver;
    private BluetoothAudioVolumeObserver btAudioVolumeObserver2;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnectionStatus();
        }
    };

    private final BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {}

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (connectedAppleDevice != null) {
                try {
                    Method getBatteryLevelMethod = connectedAppleDevice.getClass().getMethod("getBatteryLevel");
                    int batteryLevel = (int) getBatteryLevelMethod.invoke(connectedAppleDevice);
                    if (batteryLevel != -1) {
                        binding.battery.setText(batteryLevel + "%");
                    } else {
                        binding.battery.setText(getResources().getString(R.string.battery_not_available));
                    }
                } catch (Exception e) {
                    binding.battery.setText(getResources().getString(R.string.battery_not_available));
                }
            }
            bAdapter.closeProfileProxy(profile, proxy);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        Log.d(TAG, "onCreate: Apple Audio Settings started.");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Apple Audio Settings");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bAdapter = BluetoothAdapter.getDefaultAdapter();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setupUI();
        new AdsGeneral(this).loadAdaptiveBanner(binding.adContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConnectionStatus();
        registerBtReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.e(TAG, "Receiver not registered", e);
        }
        if (btAudioVolumeObserver != null) btAudioVolumeObserver.unregister();
        if (btAudioVolumeObserver2 != null) btAudioVolumeObserver2.unregister();
    }

    private void registerBtReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receiver, intentFilter);
        }
    }

    private void setupUI() {
        // Audio volume controls
        int mediaMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        binding.seekbarVolume.setMax(mediaMax);
        binding.seekbarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        binding.seekbarVolume.setOnSeekBarChangeListener(new SeekBarListener(AudioManager.STREAM_MUSIC));

        int callMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        binding.seekbarCall.setMax(callMax);
        binding.seekbarCall.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        binding.seekbarCall.setOnSeekBarChangeListener(new SeekBarListener(AudioManager.STREAM_VOICE_CALL));

        // Audio volume observers
        btAudioVolumeObserver = new BluetoothAudioVolumeObserver(this);
        btAudioVolumeObserver.register(AudioManager.STREAM_MUSIC, (current, max) -> binding.seekbarVolume.setProgress(current));
        btAudioVolumeObserver2 = new BluetoothAudioVolumeObserver(this);
        btAudioVolumeObserver2.register(AudioManager.STREAM_VOICE_CALL, (current, max) -> binding.seekbarCall.setProgress(current));

        // Profile selection removed - Apple devices handle this automatically
        // Hide profile selection UI elements if they exist
        hideProfileSelection();
    }

    /**
     * Hide manual profile selection - Apple devices handle this automatically
     */
    private void hideProfileSelection() {
        try {
            if (binding.hspLayout != null) {
                binding.hspLayout.setVisibility(View.GONE);
            }
            if (binding.a2dpLayout != null) {
                binding.a2dpLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.d(TAG, "Profile selection UI not found - already removed");
        }
    }

    private void updateConnectionStatus() {
        connectedAppleDevice = getConnectedAppleDevice();
        if (connectedAppleDevice != null && isConnected(connectedAppleDevice)) {
            binding.connectRl.setVisibility(View.VISIBLE);
            binding.disConnectRl.setVisibility(View.GONE);

            String deviceName = getAppleDeviceName(connectedAppleDevice);
            binding.name.setText(deviceName);

            // Set appropriate icon for Apple device
            setAppleDeviceIcon(binding.connectTypeLogo, connectedAppleDevice);

            // Get battery level using safer method
            bAdapter.getProfileProxy(this, serviceListener, BluetoothProfile.HEADSET);

        } else {
            binding.connectRl.setVisibility(View.GONE);
            binding.disConnectRl.setVisibility(View.VISIBLE);
            binding.bluetoothStatus.setText(getResources().getString(R.string.bbl32)); // Connect Apple device message
        }
    }

    /**
     * Get connected Apple audio device (safer method without reflection)
     */
    private BluetoothDevice getConnectedAppleDevice() {
        if (bAdapter == null) return null;

        try {
            Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
            if (pairedDevices != null) {
                for (BluetoothDevice device : pairedDevices) {
                    if (isAppleAudioDevice(device) && isConnected(device)) {
                        return device;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting connected Apple device", e);
        }
        return null;
    }

    /**
     * Check if device is an Apple audio device
     */
    private boolean isAppleAudioDevice(BluetoothDevice device) {
        if (device == null) return false;

        String name = device.getName();
        if (name == null) return false;

        String lowerName = name.toLowerCase();

        // AirPods family
        if (lowerName.contains("airpods")) return true;

        // Beats family
        if (lowerName.contains("beats")) return true;
        if (lowerName.contains("powerbeats")) return true;
        if (lowerName.contains("beatsx")) return true;

        return false;
    }

    /**
     * Get enhanced Apple device name
     */
    private String getAppleDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null || deviceName.isEmpty()) {
            return getResources().getString(R.string.apple_audio_device);
        }

        String lowerName = deviceName.toLowerCase();

        // AirPods models
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro")) return getString(R.string.airpods_pro);
            if (lowerName.contains("max")) return getString(R.string.airpods_max);
            return "AirPods";
        }

        // Beats models
        if (lowerName.contains("beats")) {
            if (lowerName.contains("solo")) return getString(R.string.beats_solo);
            if (lowerName.contains("studio")) return getString(R.string.beats_studio);
            if (lowerName.contains("powerbeats")) return getString(R.string.powerbeats);
            if (lowerName.contains("flex")) return getString(R.string.beats_flex);
            return "Beats";
        }

        // For API 30+ try alias
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String alias = device.getAlias();
            if (alias != null && !alias.isEmpty()) {
                return alias;
            }
        }

        return deviceName;
    }

    /**
     * Set appropriate icon for Apple device
     */
    private void setAppleDeviceIcon(ImageView imageView, BluetoothDevice device) {
        String name = device.getName();
        if (name != null) {
            String lowerName = name.toLowerCase();

            if (lowerName.contains("airpods")) {
                Glide.with(this).load(R.drawable.airpodes).into(imageView);
            } else if (lowerName.contains("beats")) {
                Glide.with(this).load(R.drawable.headphone).into(imageView);
            } else {
                Glide.with(this).load(R.drawable.bluetooth).into(imageView);
            }
        } else {
            Glide.with(this).load(R.drawable.bluetooth).into(imageView);
        }
    }

    /**
     * Check if device is connected (safer method)
     */
    private boolean isConnected(BluetoothDevice device) {
        try {
            // Try reflection as fallback, but don't rely on it heavily
            Method isConnectedMethod = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) isConnectedMethod.invoke(device, (Object[]) null);
        } catch (Exception e) {
            // Fallback: check bond state (not perfect but safer)
            return device.getBondState() == BluetoothDevice.BOND_BONDED;
        }
    }

    // Audio controls
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        private final int streamType;

        SeekBarListener(int streamType) {
            this.streamType = streamType;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                audioManager.setStreamVolume(streamType, progress, 0);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}