package com.hdytyldrm.batterylevel.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.adapter.PairedDeviceAdapter;
import com.hdytyldrm.batterylevel.ads.AdsGeneral;
import com.hdytyldrm.batterylevel.ads.AdsUnit;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
public class ScanDeviceActivity extends AppCompatActivity implements PairedDeviceAdapter.CreationInterface {

    private final ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private final Set<String> discoveredDevicesAddresses = new HashSet<>();

    private PairedDeviceAdapter pairedDeviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private boolean isScanning = false;
    private final Handler scanHandler = new Handler(Looper.getMainLooper());
    private static final long SCAN_PERIOD = 15000; // 15 saniye

    // UI Bileşenleri - DÜZELTİLDİ
    private Toolbar toolbar;
    private RecyclerView listRV;
    private LinearLayout scanningLayout;
    private LinearLayout noFoundLayout;
    private FloatingActionButton scanFab; // Değiştirildi
    private AdView adView;
    FrameLayout adContainer;
    private final BroadcastReceiver classicScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addDeviceToList(device);
            }
        }
    };

    private final ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addDeviceToList(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addDeviceToList(result.getDevice());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(ScanDeviceActivity.this, "BLE Taraması Başlatılamadı", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);
        EdgeToEdge.enable(this);

        initializeViews();
        setupToolbar();
        setupRecyclerView();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth desteklenmiyor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanFab.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            startCombinedScan();
        });

        // Tarama işlemini SADECE aktivite ilk oluşturulduğunda başlat
        startCombinedScan();
        new AdsGeneral(this).loadAdaptiveBanner(adContainer);


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
       // startCombinedScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCombinedScan();
        unregisterReceiver(classicScanReceiver);
    }

*/
/*
    private void startCombinedScan() {
        if (isScanning) return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Bluetooth Tarama izni gerekli", Toast.LENGTH_SHORT).show();
            return;
        }
        deviceList.clear();
        discoveredDevicesAddresses.clear();
        pairedDeviceAdapter.updateList(new ArrayList<>());
        showState(UIState.SCANNING);
        isScanning = true;

        scanHandler.postDelayed(() -> {
            if (isScanning) {
                stopCombinedScan();
                if (deviceList.isEmpty()) {
                    showState(UIState.NO_DEVICES_FOUND);
                }
            }
        }, SCAN_PERIOD);

        bleScanner.startScan(bleScanCallback);
        bluetoothAdapter.startDiscovery();
    }
*//*

private void startCombinedScan() {
    // Eğer zaten bir tarama yapılıyorsa, önce onu durdur.
    if (isScanning) {
        stopCombinedScan();
    }

    // Gerekli izinleri kontrol et
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Bluetooth Tarama izni gerekli", Toast.LENGTH_SHORT).show();
        return;
    }

    // Listeyi ve hafızayı temizle
    deviceList.clear();
    discoveredDevicesAddresses.clear();
    runOnUiThread(() -> pairedDeviceAdapter.updateList(new ArrayList<>()));

    // "Taranıyor" durumunu ve progress indicator'ı göster
    showState(UIState.SCANNING);
    isScanning = true;

    // Tarama süresi sonunda taramayı durdur
    scanHandler.postDelayed(() -> {
        if (isScanning) {
            stopCombinedScan();
            if (deviceList.isEmpty()) {
                showState(UIState.NO_DEVICES_FOUND);
            }
        }
    }, SCAN_PERIOD);

    // İki taramayı da başlat
    bleScanner.startScan(bleScanCallback);
    bluetoothAdapter.startDiscovery();
}
    private void stopCombinedScan() {
        if (!isScanning) return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        isScanning = false;
        bleScanner.stopScan(bleScanCallback);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private synchronized void addDeviceToList(BluetoothDevice device) {
        if (device == null || device.getAddress() == null) {
            return;
        }

        // İzinleri kontrol et
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Cihazın MAC adresi hafızada zaten var mı?
        if (discoveredDevicesAddresses.contains(device.getAddress())) {
            return; // Evet, var. Hiçbir şey yapma.
        }

        // Filtreleme mantığı: Cihazın adı var mı VEYA bir ses cihazı mı?
        String deviceName = device.getName();
        int deviceClass = device.getBluetoothClass().getMajorDeviceClass();
        if (!TextUtils.isEmpty(deviceName) || deviceClass == android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO) {
            // Cihaz geçerli. Hafızaya ve listeye ekle.
            discoveredDevicesAddresses.add(device.getAddress());
            deviceList.add(device);
            runOnUiThread(() -> {
                pairedDeviceAdapter.updateList(new ArrayList<>(deviceList));
                showState(UIState.DEVICES_FOUND);
            });
        }
    }
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        listRV = findViewById(R.id.listRV);
        scanningLayout = findViewById(R.id.scanningLayout);
        noFoundLayout = findViewById(R.id.noFoundLayout);
        scanFab = findViewById(R.id.scanFab);
        adView = findViewById(R.id.adView);// Değiştirildi
        adContainer=findViewById(R.id.ad_view_container);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        pairedDeviceAdapter = new PairedDeviceAdapter(this, deviceList, this);
        listRV.setAdapter(pairedDeviceAdapter);
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(classicScanReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(classicScanReceiver, filter);
        }
    }

    private enum UIState { SCANNING, DEVICES_FOUND, NO_DEVICES_FOUND }

    private void showState(UIState state) {
        scanningLayout.setVisibility(state == UIState.SCANNING ? View.VISIBLE : View.GONE);
        noFoundLayout.setVisibility(state == UIState.NO_DEVICES_FOUND ? View.VISIBLE : View.GONE);
        listRV.setVisibility(state == UIState.DEVICES_FOUND ? View.VISIBLE : View.GONE);

        // FAB'ı animasyonla gösterip gizle
        if (state == UIState.SCANNING) {
            scanFab.hide();
        } else {
            scanFab.show();
        }
    }

    @Override
    public void onConnectDisconnectClicked(BluetoothDevice bluetoothDevice) {
        try {
            stopCombinedScan();
            Toast.makeText(this, (bluetoothDevice.getName() != null ? bluetoothDevice.getName() : bluetoothDevice.getAddress()) + " ile eşleştiriliyor...", Toast.LENGTH_SHORT).show();
            bluetoothDevice.createBond();
        } catch (Exception e) {
            Toast.makeText(this, "Eşleştirme başlatılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isDeviceConnected(BluetoothDevice bluetoothDevice) {
        return false;
    }
}*/
/**
 * Scan Device Activity - Apple Audio Devices Only
 * Focuses on discovering Apple audio devices safely
 * Removed generic device scanning and manual bonding for Play Store compliance
 */
public class ScanDeviceActivity extends AppCompatActivity implements PairedDeviceAdapter.CreationInterface {
    private static final String TAG = "AppleDeviceScanner";

    private final ArrayList<BluetoothDevice> discoveredAppleDevices = new ArrayList<>();
    private final Set<String> discoveredDevicesAddresses = new HashSet<>();

    private PairedDeviceAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private boolean isScanning = false;
    private final Handler scanHandler = new Handler(Looper.getMainLooper());
    private static final long SCAN_PERIOD = 15000; // 15 seconds

    // UI Components
    private Toolbar toolbar;
    private RecyclerView listRV;
    private LinearLayout scanningLayout;
    private LinearLayout noFoundLayout;
    private FloatingActionButton scanFab;
    private AdView adView;
    private FrameLayout adContainer;

    // Apple device manufacturer ID and data patterns
    private static final int APPLE_MANUFACTURER_ID = 76;
    private static final String[] APPLE_DEVICE_PATTERNS = {
            "airpods", "beats", "powerbeats", "beatsx", "beats x", "beats flex",
            "beats solo", "beats studio"
    };

    private final BroadcastReceiver classicScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isAppleAudioDevice(device)) {
                    addAppleDeviceToList(device);
                }
            }
        }
    };

    private final ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (isAppleDeviceResult(result)) {
                addAppleDeviceToList(result.getDevice());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                if (isAppleDeviceResult(result)) {
                    addAppleDeviceToList(result.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "BLE scan failed with error code: " + errorCode);
            Toast.makeText(ScanDeviceActivity.this, "Apple device scan failed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);
        EdgeToEdge.enable(this);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        initializeBluetooth();
        setupScanButton();
        loadBannerAd();

        // Start initial scan for Apple devices
        startAppleDeviceScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAppleDeviceScan();
        unregisterReceivers();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        listRV = findViewById(R.id.listRV);
        scanningLayout = findViewById(R.id.scanningLayout);
        noFoundLayout = findViewById(R.id.noFoundLayout);
        scanFab = findViewById(R.id.scanFab);
        adView = findViewById(R.id.adView);
        adContainer = findViewById(R.id.ad_view_container);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scan Apple Devices");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        deviceAdapter = new PairedDeviceAdapter(this, discoveredAppleDevices, this);
        listRV.setAdapter(deviceAdapter);
    }

    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void setupScanButton() {
        scanFab.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            startAppleDeviceScan();
        });
    }

    private void loadBannerAd() {
        new AdsGeneral(this).loadAdaptiveBanner(adContainer);
    }

    /**
     * Start scanning for Apple audio devices only
     */
    private void startAppleDeviceScan() {
        if (isScanning) {
            stopAppleDeviceScan();
        }

        if (!checkPermissions()) {
            Toast.makeText(this, "Bluetooth scanning permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear previous results
        discoveredAppleDevices.clear();
        discoveredDevicesAddresses.clear();
        runOnUiThread(() -> deviceAdapter.updateList(new ArrayList<>()));

        showState(UIState.SCANNING);
        isScanning = true;

        // Set scan timeout
        scanHandler.postDelayed(() -> {
            if (isScanning) {
                stopAppleDeviceScan();
                if (discoveredAppleDevices.isEmpty()) {
                    showState(UIState.NO_DEVICES_FOUND);
                }
            }
        }, SCAN_PERIOD);

        // Start BLE scan for Apple devices
        startBLEScan();

        // Start classic scan as backup
        startClassicScan();
    }

    private void stopAppleDeviceScan() {
        if (!isScanning) return;

        isScanning = false;
        stopBLEScan();
        stopClassicScan();
        scanHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Start BLE scan specifically for Apple devices
     */
    private void startBLEScan() {
        if (!checkPermissions() || bleScanner == null) return;

        try {
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();

            List<ScanFilter> filters = createAppleScanFilters();
            bleScanner.startScan(filters, settings, bleScanCallback);
            Log.d(TAG, "BLE scan started for Apple devices");
        } catch (Exception e) {
            Log.e(TAG, "Error starting BLE scan", e);
        }
    }

    private void stopBLEScan() {
        if (!checkPermissions() || bleScanner == null) return;

        try {
            bleScanner.stopScan(bleScanCallback);
            Log.d(TAG, "BLE scan stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping BLE scan", e);
        }
    }

    /**
     * Create scan filters for Apple devices
     */
    private List<ScanFilter> createAppleScanFilters() {
        ScanFilter.Builder builder = new ScanFilter.Builder();

        // Filter by Apple manufacturer ID
        byte[] manufacturerData = new byte[]{0x07, 0x19}; // Apple beacon signature
        byte[] manufacturerDataMask = new byte[]{(byte) 0xFF, (byte) 0xFF};

        builder.setManufacturerData(APPLE_MANUFACTURER_ID, manufacturerData, manufacturerDataMask);

        return Collections.singletonList(builder.build());
    }

    /**
     * Start classic Bluetooth discovery as backup
     */
    private void startClassicScan() {
        if (!checkPermissions()) return;

        try {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
            Log.d(TAG, "Classic Bluetooth discovery started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting classic scan", e);
        }
    }

    private void stopClassicScan() {
        if (!checkPermissions()) return;

        try {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping classic scan", e);
        }
    }

    /**
     * Check if discovered device is an Apple audio device
     */
    private boolean isAppleAudioDevice(BluetoothDevice device) {
        if (device == null) return false;

        String deviceName = device.getName();
        if (TextUtils.isEmpty(deviceName)) return false;

        String lowerName = deviceName.toLowerCase();

        // Check against Apple device patterns
        for (String pattern : APPLE_DEVICE_PATTERNS) {
            if (lowerName.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if BLE scan result is from Apple device
     */
    private boolean isAppleDeviceResult(ScanResult result) {
        if (result == null || result.getDevice() == null) return false;

        // Check manufacturer data for Apple signature
        if (result.getScanRecord() != null) {
            byte[] manufacturerData = result.getScanRecord().getManufacturerSpecificData(APPLE_MANUFACTURER_ID);
            if (manufacturerData != null && manufacturerData.length > 0) {
                return true;
            }
        }

        // Fallback: check device name
        return isAppleAudioDevice(result.getDevice());
    }

    /**
     * Add discovered Apple device to list (thread-safe)
     */
    private synchronized void addAppleDeviceToList(BluetoothDevice device) {
        if (device == null || device.getAddress() == null) return;

        if (!checkPermissions()) return;

        // Avoid duplicates
        if (discoveredDevicesAddresses.contains(device.getAddress())) {
            return;
        }

        // Double-check it's an Apple device
        if (!isAppleAudioDevice(device)) {
            return;
        }

        discoveredDevicesAddresses.add(device.getAddress());
        discoveredAppleDevices.add(device);

        Log.d(TAG, "Apple device found: " + device.getName());

        runOnUiThread(() -> {
            deviceAdapter.updateList(new ArrayList<>(discoveredAppleDevices));
            showState(UIState.DEVICES_FOUND);
        });
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(classicScanReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(classicScanReceiver, filter);
        }
    }

    private void unregisterReceivers() {
        try {
            unregisterReceiver(classicScanReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered", e);
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private enum UIState { SCANNING, DEVICES_FOUND, NO_DEVICES_FOUND }

    private void showState(UIState state) {
        scanningLayout.setVisibility(state == UIState.SCANNING ? View.VISIBLE : View.GONE);
        noFoundLayout.setVisibility(state == UIState.NO_DEVICES_FOUND ? View.VISIBLE : View.GONE);
        listRV.setVisibility(state == UIState.DEVICES_FOUND ? View.VISIBLE : View.GONE);

        // Show/hide FAB with animation
        if (state == UIState.SCANNING) {
            scanFab.hide();
        } else {
            scanFab.show();
        }
    }

    // ===== PairedDeviceAdapter.CreationInterface Implementation =====

    @Override
    public void onConnectDisconnectClicked(BluetoothDevice bluetoothDevice) {
        try {
            stopAppleDeviceScan();

            String deviceName = bluetoothDevice.getName() != null ?
                    bluetoothDevice.getName() : "Apple Device";

            // Show informational message instead of manual pairing
            Toast.makeText(this,
                    "To pair with " + deviceName + ", please use your device's Bluetooth settings.",
                    Toast.LENGTH_LONG).show();

            // Open system Bluetooth settings for safer pairing
            openBluetoothSettings();

        } catch (Exception e) {
            Toast.makeText(this, "Unable to initiate pairing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error in connect/disconnect", e);
        }
    }

    @Override
    public boolean isDeviceConnected(BluetoothDevice bluetoothDevice) {
        // For scan activity, devices are not connected yet
        return false;
    }

    /**
     * Open system Bluetooth settings for manual pairing
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
}