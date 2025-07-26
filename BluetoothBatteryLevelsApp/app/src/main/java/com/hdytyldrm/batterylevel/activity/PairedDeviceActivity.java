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
import com.hdytyldrm.batterylevel.connections.BluetoothConnectDisconnect;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

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


  @Override
  public boolean isDeviceConnected(BluetoothDevice device) {
      try {
          BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

          // Ana audio profiller
          int a2dpState = bluetoothManager.getConnectionState(device, BluetoothProfile.A2DP);
          int headsetState = bluetoothManager.getConnectionState(device, BluetoothProfile.HEADSET);

          // Ek profiller (opsiyonel)
          int hidState = bluetoothManager.getConnectionState(device, BluetoothProfile.HID_DEVICE);
          int gattState = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT);

          return (a2dpState == BluetoothProfile.STATE_CONNECTED) ||
                  (headsetState == BluetoothProfile.STATE_CONNECTED) ||
                  (hidState == BluetoothProfile.STATE_CONNECTED) ||
                  (gattState == BluetoothProfile.STATE_CONNECTED);
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
}