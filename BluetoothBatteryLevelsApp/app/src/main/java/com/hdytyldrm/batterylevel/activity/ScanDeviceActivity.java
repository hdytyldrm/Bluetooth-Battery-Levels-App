package com.hdytyldrm.batterylevel.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
*/
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
}