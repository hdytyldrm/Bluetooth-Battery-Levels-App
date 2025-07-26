package com.demo.bluetoothbatterylevel.activity;

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
import com.demo.bluetoothbatterylevel.R;
import com.demo.bluetoothbatterylevel.ads.AdsGeneral;
import com.demo.bluetoothbatterylevel.audio.BluetoothAudioVolumeObserver;
import com.demo.bluetoothbatterylevel.connections.BluetoothConnectDisconnect;
import com.demo.bluetoothbatterylevel.databinding.ActivitySettingBinding;
import com.demo.bluetoothbatterylevel.utils.SpManager;

import java.lang.reflect.Method;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";

    private ActivitySettingBinding binding;
    private AudioManager audioManager;
    private BluetoothAdapter bAdapter;
    private BluetoothDevice connectedDevice;
    private BluetoothAudioVolumeObserver btAudioVolumeObserver;
    private BluetoothAudioVolumeObserver btAudioVolumeObserver2;
    private boolean isSwitchingProfile = false;

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
            if (connectedDevice != null) {
                try {
                    Method getBatteryLevelMethod = connectedDevice.getClass().getMethod("getBatteryLevel");
                    int batteryLevel = (int) getBatteryLevelMethod.invoke(connectedDevice);
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
        Log.d(TAG, "onCreate: Aktivite başlatıldı.");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        unregisterReceiver(receiver);
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
        int mediaMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        binding.seekbarVolume.setMax(mediaMax);
        binding.seekbarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        binding.seekbarVolume.setOnSeekBarChangeListener(new SeekBarListener(AudioManager.STREAM_MUSIC));

        int callMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        binding.seekbarCall.setMax(callMax);
        binding.seekbarCall.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        binding.seekbarCall.setOnSeekBarChangeListener(new SeekBarListener(AudioManager.STREAM_VOICE_CALL));

        btAudioVolumeObserver = new BluetoothAudioVolumeObserver(this);
        btAudioVolumeObserver.register(AudioManager.STREAM_MUSIC, (current, max) -> binding.seekbarVolume.setProgress(current));
        btAudioVolumeObserver2 = new BluetoothAudioVolumeObserver(this);
        btAudioVolumeObserver2.register(AudioManager.STREAM_VOICE_CALL, (current, max) -> binding.seekbarCall.setProgress(current));

        // Yeni Tıklama Listener'ları
        binding.hspLayout.setOnClickListener(v -> handleProfileClick("HSP"));
        binding.a2dpLayout.setOnClickListener(v -> handleProfileClick("A2DP"));
    }

    private void handleProfileClick(String newProfile) {
        Log.d(TAG, newProfile + " profiline tıklandı.");

        if (isSwitchingProfile) {
            Log.d(TAG, "Zaten bir profil değiştirme işlemi sürüyor, işlem yoksayıldı.");
            return;
        }

        if (connectedDevice == null || !isConnected(connectedDevice)) {
            Log.e(TAG, "Hata: Cihaz bağlı değil, işlem iptal.");
            Toast.makeText(this, getResources().getString(R.string.bbl32), Toast.LENGTH_SHORT).show();
            return;
        }

        isSwitchingProfile = true;
        updateProfileSelectionUI(newProfile);
        switchProfile(newProfile);
    }

    private void updateProfileSelectionUI(String selectedProfile) {
        binding.hspSelectedIcon.setVisibility("HSP".equals(selectedProfile) ? View.VISIBLE : View.GONE);
        binding.a2dpSelectedIcon.setVisibility("A2DP".equals(selectedProfile) ? View.VISIBLE : View.GONE);
    }

    private void updateConnectionStatus() {
        connectedDevice = getConnectedBluetoothDevice();
        if (connectedDevice != null && isConnected(connectedDevice)) {
            binding.connectRl.setVisibility(View.VISIBLE);
            binding.disConnectRl.setVisibility(View.GONE);

            String deviceName = connectedDevice.getName();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && (deviceName == null || deviceName.isEmpty())) {
                deviceName = connectedDevice.getAlias();
            }
            if (deviceName == null || deviceName.isEmpty()) {
                deviceName = connectedDevice.getAddress();
            }
            binding.name.setText(deviceName);
            checkBluetoothType(binding.connectTypeLogo, connectedDevice);

            bAdapter.getProfileProxy(this, serviceListener, BluetoothProfile.HEADSET);

            // Mevcut seçili profili UI'da göster
            updateProfileSelectionUI(SpManager.getConnectType());

        } else {
            binding.connectRl.setVisibility(View.GONE);
            binding.disConnectRl.setVisibility(View.VISIBLE);
            binding.bluetoothStatus.setText(getResources().getString(R.string.bbl20));
            updateProfileSelectionUI(null); // Hiçbiri seçili değil
        }
    }

    private void switchProfile(String newProfile) {
        Log.d(TAG, newProfile + " profiline geçiliyor...");
        SpManager.setConnectType(newProfile);
        disconnectDevice(connectedDevice);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            connectDevice(connectedDevice, 1);
            isSwitchingProfile = false;
            Log.d(TAG, "Yeniden bağlanma komutu gönderildi. Kilit açıldı.");
        }, 1000);
    }

    // --- Yardımcı Metotlar ---

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        private final int streamType;
        SeekBarListener(int streamType) { this.streamType = streamType; }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { if(fromUser) audioManager.setStreamVolume(streamType, progress, 0); }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private void connectDevice(BluetoothDevice device, int profile) {
        new BluetoothConnectDisconnect(this).manageConnection(profile, device.getAddress());
    }

    private void disconnectDevice(BluetoothDevice device) {
        new BluetoothConnectDisconnect(this).manageConnection(2, device.getAddress());
    }

    private BluetoothDevice getConnectedBluetoothDevice() {
        if (bAdapter == null) return null;
        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
        if (pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (isConnected(device)) {
                    return device;
                }
            }
        }
        return null;
    }

    private boolean isConnected(BluetoothDevice device) {
        try {
            Method isConnectedMethod = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) isConnectedMethod.invoke(device, (Object[]) null);
        } catch (Exception e) {
            return false;
        }
    }

    private void checkBluetoothType(ImageView imageView, BluetoothDevice device) {
        // Bu metot olduğu gibi kalabilir
        int deviceClass = device.getBluetoothClass().getDeviceClass();
        if (device.getName() != null && device.getName().toLowerCase().contains("airpods")) {
            Glide.with(this).load(R.drawable.airpodes).into(imageView);
        } else if (deviceClass == 1048 || deviceClass == 1028 || deviceClass == 1056) {
            Glide.with(this).load(R.drawable.headphone).into(imageView);
        } else if (deviceClass == 1796) {
            Glide.with(this).load(R.drawable.watch).into(imageView);
        } else {
            Glide.with(this).load(R.drawable.bluetooth).into(imageView);
        }
    }
}