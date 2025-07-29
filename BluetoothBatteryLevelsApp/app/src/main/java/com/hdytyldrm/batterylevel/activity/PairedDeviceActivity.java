package com.hdytyldrm.batterylevel.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.adapter.PairedDeviceAdapter;
import com.hdytyldrm.batterylevel.ads.AdsGeneral;
import com.hdytyldrm.batterylevel.ads.AdsUnit;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/*
public class PairedDeviceActivity extends AppCompatActivity implements PairedDeviceAdapter.CreationInterface {

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private PairedDeviceAdapter pairedDeviceAdapter;

    // View'ları tanımlıyoruz
    private Toolbar toolbar;
    private RecyclerView listRV;
    private LinearLayout noDataView;
    private FrameLayout adContainer;
    private AdView adView;
    private final BroadcastReceiver aclReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Bu receiver mantığı genellikle aynı kalabilir
            // Her bağlantı durumunda listeyi yeniden yükler
            loadPairedDevices();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Yeni layout'u set ediyoruz
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_paired_device);

        // View'ları ID'leri ile buluyoruz
        toolbar = findViewById(R.id.toolbar);
        listRV = findViewById(R.id.listRV);
        noDataView = findViewById(R.id.noData);
        adContainer = findViewById(R.id.adContainer);
        adView = findViewById(R.id.adView);
        // Toolbar'ı ayarlıyoruz
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Reklamları yüklüyoruz (eski kodunuza göre)
        // AdsCommon.RegulerBanner(this, admob_banner, adContainer, qureka);
        // NOT: Reklam yerleşiminiz FrameLayout (adContainer) içine olacak şekilde düzenlenmelidir.

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth bu cihazda desteklenmiyor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Adapter'ı ve RecyclerView'ı ayarlıyoruz
        pairedDeviceAdapter = new PairedDeviceAdapter(this, deviceList, this);
        listRV.setAdapter(pairedDeviceAdapter);

        // Eşleştirilmiş cihazları yüklüyoruz
        loadPairedDevices();

        // Broadcast Receiver'ı kaydediyoruz
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(aclReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(aclReceiver, filter);
        }
        new AdsGeneral(this).loadAdaptiveBanner(adContainer);

    }

    private void loadPairedDevices() {
        deviceList.clear();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices != null && pairedDevices.size() > 0) {
            deviceList.addAll(pairedDevices);
            noDataView.setVisibility(View.GONE);
            listRV.setVisibility(View.VISIBLE);
        } else {
            noDataView.setVisibility(View.VISIBLE);
            listRV.setVisibility(View.GONE);
        }
        pairedDeviceAdapter.updateList(new ArrayList<>(deviceList));
    }

    // --- PairedDeviceAdapter.CreationInterface Metotları ---

    @Override
    public void onConnectDisconnectClicked(BluetoothDevice bluetoothDevice) {
        if (isDeviceConnected(bluetoothDevice)) {
            disconnectDevice(bluetoothDevice);
        } else {
            connectDevice(bluetoothDevice);
        }
    }

   */
/* @Override
    public boolean isDeviceConnected(BluetoothDevice device) {
        try {
            // isConnected metodunu yansıma (reflection) ile çağırma
            Method method = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            return false;
        }
    }*//*


    @Override
    public boolean isDeviceConnected(BluetoothDevice device) {
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            int a2dpState = bluetoothManager.getConnectionState(device, BluetoothProfile.A2DP);
            int headsetState = bluetoothManager.getConnectionState(device, BluetoothProfile.HEADSET);
            return (a2dpState == BluetoothProfile.STATE_CONNECTED) ||
                    (headsetState == BluetoothProfile.STATE_CONNECTED);
        } catch (Exception e) {
            return false;
        }
    }

    // --- Bağlantı Metotları (Eski kodunuzdan) ---

    private void connectDevice(BluetoothDevice bluetoothDevice) {
        // Eski kodunuzdaki bağlantı mantığı
        BluetoothConnectDisconnect connector = new BluetoothConnectDisconnect(this);
        connector.manageConnection(1, bluetoothDevice.getAddress());
        connector.method3(1, bluetoothDevice.getAddress());
        Toast.makeText(this, bluetoothDevice.getName() + " bağlanıyor...", Toast.LENGTH_SHORT).show();
    }

    public void disconnectDevice(BluetoothDevice bluetoothDevice) {
        // Eski kodunuzdaki bağlantıyı kesme mantığı
        BluetoothConnectDisconnect connector = new BluetoothConnectDisconnect(this);
        connector.manageConnection(2, bluetoothDevice.getAddress());
        connector.manageConnection(1, bluetoothDevice.getAddress());
        Toast.makeText(this, bluetoothDevice.getName() + " bağlantısı kesiliyor...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Receiver'ı unregister etmeyi unutmuyoruz
        unregisterReceiver(aclReceiver);
    }
}*/

/**
 * Paired Device Activity - Apple Audio Devices Only
 * Shows only Apple audio devices (AirPods, Beats, etc.)
 * Removed manual connection/disconnection for Play Store compliance
 */
public class PairedDeviceActivity extends AppCompatActivity implements PairedDeviceAdapter.CreationInterface {
    private static final String TAG = "ApplePairedDevices";

    private ArrayList<BluetoothDevice> appleDeviceList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private PairedDeviceAdapter pairedDeviceAdapter;

    // UI Components
    private Toolbar toolbar;
    private RecyclerView listRV;
    private View noDataView;
    private FrameLayout adContainer;
    private AdView adView;

    // Supported Apple audio device patterns
    private static final String[] APPLE_DEVICE_PATTERNS = {
            "airpods", "beats", "powerbeats", "beatsx", "beats x", "beats flex",
            "beats solo", "beats studio"
    };

    private final BroadcastReceiver aclReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (device != null && isAppleAudioDevice(device)) {
                Log.d(TAG, "Apple device state changed: " + action);
                loadAppleDevices();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_paired_device);

        initializeViews();
        setupToolbar();
        initializeBluetooth();
        setupRecyclerView();
        loadAppleDevices();
        loadBannerAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBluetoothReceiver();
        loadAppleDevices(); // Refresh on resume
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBluetoothReceiver();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        listRV = findViewById(R.id.listRV);
        noDataView = findViewById(R.id.noData);
        adContainer = findViewById(R.id.adContainer);
        adView = findViewById(R.id.adView);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Apple Audio Devices");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        pairedDeviceAdapter = new PairedDeviceAdapter(this, appleDeviceList, this);
        listRV.setAdapter(pairedDeviceAdapter);
    }

    private void loadAppleDevices() {
        appleDeviceList.clear();

        if (bluetoothAdapter == null) {
            showNoDataState();
            return;
        }

        try {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices != null) {
                for (BluetoothDevice device : pairedDevices) {
                    if (isAppleAudioDevice(device)) {
                        appleDeviceList.add(device);
                        Log.d(TAG, "Found Apple device: " + device.getName());
                    }
                }
            }

            if (appleDeviceList.isEmpty()) {
                showNoDataState();
            } else {
                showDataState();
                pairedDeviceAdapter.updateList(new ArrayList<>(appleDeviceList));
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied for Bluetooth access", e);
            Toast.makeText(this, "Bluetooth permission required", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Enhanced Apple audio device detection
     */
    private boolean isAppleAudioDevice(BluetoothDevice device) {
        if (device == null) return false;

        String deviceName = device.getName();
        if (deviceName == null) return false;

        String lowerName = deviceName.toLowerCase();

        // Check against known Apple device patterns
        for (String pattern : APPLE_DEVICE_PATTERNS) {
            if (lowerName.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    private void showNoDataState() {
        noDataView.setVisibility(View.VISIBLE);
        listRV.setVisibility(View.GONE);

        // Update no data message for Apple devices
        // Assuming you have a TextView in noData layout
        // You might need to update the layout to show Apple-specific message
    }

    private void showDataState() {
        noDataView.setVisibility(View.GONE);
        listRV.setVisibility(View.VISIBLE);
    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(aclReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(aclReceiver, filter);
        }
    }

    private void unregisterBluetoothReceiver() {
        try {
            unregisterReceiver(aclReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered", e);
        }
    }

    private void loadBannerAd() {
        new AdsGeneral(this).loadAdaptiveBanner(adContainer);
    }

    // ===== PairedDeviceAdapter.CreationInterface Implementation =====

    @Override
    public void onConnectDisconnectClicked(BluetoothDevice bluetoothDevice) {
        // Removed manual connection/disconnection for Play Store compliance
        // Instead, show informational message
        String deviceName = bluetoothDevice.getName() != null ?
                bluetoothDevice.getName() : "Apple Device";

        if (isDeviceConnected(bluetoothDevice)) {
            Toast.makeText(this,
                    deviceName + " is connected. Use device settings to disconnect.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "To connect " + deviceName + ", please use your device's Bluetooth settings.",
                    Toast.LENGTH_LONG).show();

            // Optionally open system Bluetooth settings
            openBluetoothSettings();
        }
    }

    @Override
    public boolean isDeviceConnected(BluetoothDevice device) {
        try {
            // Safer connection check without heavy reflection usage
            Method method = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            // Fallback: check bond state
            return device.getBondState() == BluetoothDevice.BOND_BONDED;
        }
    }

    /**
     * Open system Bluetooth settings for manual connection
     */
    private void openBluetoothSettings() {
        try {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Bluetooth settings", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get enhanced Apple device name for display
     */
    public String getEnhancedAppleDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null || deviceName.isEmpty()) {
            return getString(R.string.apple_audio_device);
        }

        String lowerName = deviceName.toLowerCase();

        // AirPods family
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro")) return getString(R.string.airpods_pro);
            if (lowerName.contains("max")) return getString(R.string.airpods_max);
            return "AirPods";
        }

        // Beats family
        if (lowerName.contains("beats")) {
            if (lowerName.contains("solo")) return getString(R.string.beats_solo);
            if (lowerName.contains("studio")) return getString(R.string.beats_studio);
            if (lowerName.contains("powerbeats")) return getString(R.string.powerbeats);
            if (lowerName.contains("flex")) return getString(R.string.beats_flex);
            return "Beats";
        }

        return deviceName;
    }
}