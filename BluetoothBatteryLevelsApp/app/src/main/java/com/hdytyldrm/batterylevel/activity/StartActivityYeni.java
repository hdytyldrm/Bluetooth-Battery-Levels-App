package com.hdytyldrm.batterylevel.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.ads.AdsGeneral;
import com.hdytyldrm.batterylevel.ads.CounterManager;
import com.hdytyldrm.batterylevel.ads.UnifiedInterstitialAdManager;
import com.hdytyldrm.batterylevel.model.BatteryData;
import com.hdytyldrm.batterylevel.service.ServiceDebugHelper;
import com.hdytyldrm.batterylevel.service.UnifiedBluetoothService;
import com.hdytyldrm.batterylevel.utils.AboutBottomSheetFragment;
import com.hdytyldrm.batterylevel.utils.BaseActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class StartActivityYeni extends BaseActivity {
    private static final String TAG = "StartActivityYeni";

    // Permission codes
    private static final int PERMISSION_REQUEST_CODE = 123;

    // UI Components (Manual findViewById instead of binding)
    private TextView deviceTitle;
    private ImageView airpodsImage;
    private MaterialCardView batteryCard;  // MaterialCardView olarak değiştir
    private LinearLayout noConnectionSection;
    private LinearLayout batteryLevelsSection;
    private LinearLayout leftEarbudSection;
    private LinearLayout rightEarbudSection;
    private LinearLayout caseSection;
    private View leftBatteryIcon;
    private View rightBatteryIcon;
    private View caseBatteryIcon;
    private TextView leftBatteryText;
    private TextView rightBatteryText;
    private TextView caseBatteryText;
    private MaterialCardView notificationSettingsCard;
    private SwitchCompat notificationSwitch;
    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNav;
    private FloatingActionButton bluetoothFab;
    private MaterialCardView pd;
    private MaterialCardView sd;
    private MaterialCardView volume;

    // System components
    private BluetoothAdapter bluetoothAdapter;

    // Current state
    private BatteryData currentBatteryData;
    private boolean isServiceRunning = false;

    // Permissions array based on Android version
    private String[] requiredPermissions;
    Handler handler = new Handler();

    private LinearLayout genericDeviceSection;
    private ImageView genericBatteryIcon;
    private TextView genericBatteryText;
    private LinearLayout infoButton;
    private ImageView infoIcon;
    private TextView infoText;
    private LinearLayout rateButton;
    private ImageView rateIcon;
    private TextView rateText;
    private FrameLayout adContainer;

    private UnifiedInterstitialAdManager adManager;
    // Service communication
    private BroadcastReceiver batteryUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UnifiedBluetoothService.ACTION_BATTERY_UPDATE.equals(action)) {
                BatteryData batteryData = intent.getParcelableExtra(UnifiedBluetoothService.EXTRA_BATTERY_DATA);

                if (batteryData != null) {
                    Log.d(TAG, "✅ Battery update received: " + batteryData.toString());
                    // UI thread'de çalıştığından emin ol
                    runOnUiThread(() -> handleBatteryUpdate(batteryData));
                } else {
                    Log.e(TAG, "❌ Battery data is NULL in intent!");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view without data binding
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_start_sekiz);

        Log.d(TAG, "🚀 StartActivityYeni initializing...");

        // Initialize UI components manually
        initializeViews();
        adManager = UnifiedInterstitialAdManager.getInstance(this);
        initializeComponents();
        setupPermissions();
        setupUI();
        updateBottomNavSelection(true); // Başlangıçta Ana Sayfa'yı aktif yap

        loadBannerAd();

        // DEBUGGING: Service durumunu kontrol et
        new Handler().postDelayed(() -> {
            ServiceDebugHelper.fullDebugReport(this);
            // YENI: Service'den son durumu iste
            requestCurrentBatteryStatus();
        }, 1000);

        // Permissions kontrolü
        if (checkAllPermissions()) {
            initializeBluetooth();
            // Service'i otomatik başlat
            Log.d(TAG, "🔧 Auto-starting service for testing...");
            startMonitoringService();
        } else {
            requestRequiredPermissions();
        }

        Log.d(TAG, "✅ StartActivityYeni initialized");
    }

    // Initialize all views manually (since no data binding)
    private void initializeViews() {
        deviceTitle = findViewById(R.id.deviceTitle);
     //   airpodsImage = findViewById(R.id.airpodsImage);
        batteryCard = findViewById(R.id.batteryCard);  // MaterialCardView
        noConnectionSection = findViewById(R.id.noConnectionSection);
        batteryLevelsSection = findViewById(R.id.batteryLevelsSection);
        leftEarbudSection = findViewById(R.id.leftEarbudSection);
        rightEarbudSection = findViewById(R.id.rightEarbudSection);
        caseSection = findViewById(R.id.caseSection);
        leftBatteryIcon = findViewById(R.id.leftBatteryIcon);
        rightBatteryIcon = findViewById(R.id.rightBatteryIcon);
        caseBatteryIcon = findViewById(R.id.caseBatteryIcon);
        leftBatteryText = findViewById(R.id.leftBatteryText);
        rightBatteryText = findViewById(R.id.rightBatteryText);
        caseBatteryText = findViewById(R.id.caseBatteryText);
        notificationSettingsCard = findViewById(R.id.notificationSettingsCard);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        bottomNav = findViewById(R.id.bottomNav);
        bluetoothFab = findViewById(R.id.bluetoothFab);
        pd = findViewById(R.id.pd);
        sd = findViewById(R.id.sd);
        volume=findViewById(R.id.volume);
        genericDeviceSection = findViewById(R.id.genericDeviceSection);
        genericBatteryIcon = findViewById(R.id.genericBatteryIcon);
        genericBatteryText = findViewById(R.id.genericBatteryText);
        infoButton = findViewById(R.id.nav_info);
        infoIcon = findViewById(R.id.nav_info_icon);
        infoText = findViewById(R.id.nav_info_text);
        rateButton = findViewById(R.id.nav_rate_button);
        rateIcon = findViewById(R.id.nav_rate_icon);
        rateText = findViewById(R.id.nav_rate_text);
        adContainer = findViewById(R.id.ad_view_container);
        Log.d(TAG, "✅ Views initialized manually");
    }

    private void requestCurrentBatteryStatus() {
        // Service'e mevcut durumu sorgulamak için broadcast gönder
        Intent intent = new Intent("com.hdytyldrm.batterylevel.REQUEST_BATTERY_STATUS");
        sendBroadcast(intent);
        Log.d(TAG, "📡 Requested current battery status from service");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Service update receiver'ı register et
        registerBatteryUpdateReceiver();

        // Service durumunu kontrol et ve UI'ı güncelle
        checkServiceStatus();
        // YENI: Service'den son durumu iste
        handler.postDelayed(() -> {
            requestCurrentBatteryStatus();
        }, 500);

        Log.d(TAG, "📱 Activity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Receiver'ı unregister et
        unregisterBatteryUpdateReceiver();

        Log.d(TAG, "⏸️ Activity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "🛑 Activity destroyed");
    }

    // ===== INITIALIZATION =====

    private void initializeComponents() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        currentBatteryData = new BatteryData(); // Disconnected state
    }
    private void loadBannerAd() {
        // Yeni AdsGeneral metodunu doğru parametreyle çağır
        new AdsGeneral(this).loadAdaptiveBanner(adContainer);
    }
    private void setupPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            requiredPermissions = new String[]{
                    "android.permission.BLUETOOTH",
                    "android.permission.BLUETOOTH_ADMIN",
                    "android.permission.BLUETOOTH_CONNECT",
                    "android.permission.BLUETOOTH_SCAN",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.POST_NOTIFICATIONS"
            };
        } else if (Build.VERSION.SDK_INT >= 31) {
            requiredPermissions = new String[]{
                    "android.permission.BLUETOOTH",
                    "android.permission.BLUETOOTH_ADMIN",
                    "android.permission.BLUETOOTH_CONNECT",
                    "android.permission.BLUETOOTH_SCAN",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"
            };
        } else {
            requiredPermissions = new String[]{
                    "android.permission.BLUETOOTH",
                    "android.permission.BLUETOOTH_ADMIN",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"
            };
        }
    }

    private void setupUI() {
        // Initial UI state
        updateUIForDisconnectedState();

        // Notification switch
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (checkAllPermissions()) {
                    startMonitoringService();
                } else {
                    notificationSwitch.setChecked(false);
                    requestRequiredPermissions();
                }
            } else {
                stopMonitoringService();
            }
        });

        // Bluetooth FAB
        bluetoothFab.setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
                showToast("Bluetooth not supported on this device");
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                requestBluetoothEnable();
            } else {
                // Bluetooth açık, cihaz arama activity'sine git
                startActivity(new Intent(this, ScanDeviceActivity.class));
            }
        });
        infoButton.setOnClickListener(v -> {
            updateBottomNavSelection(true); // Ana Sayfa'yı aktif yap
           // Toast.makeText(this, "Ana Sayfa", Toast.LENGTH_SHORT).show();
            showAboutBottomSheet();
        });

        rateButton.setOnClickListener(v -> {
            updateBottomNavSelection(false); // Ayarlar'ı aktif yap
          //  Toast.makeText(this, "Ayarlar", Toast.LENGTH_SHORT).show();
            showInAppReview();

        });
        // Card click handlers
        pd.setOnClickListener(v -> {
            Log.d(TAG, "🖱️ PD card clicked!");

           /* if (checkAllPermissions()) {
                Log.d(TAG, "✅ All permissions OK, launching PairedDeviceActivity_iki");

                try {
                    Intent intent = new Intent(StartActivityYeni.this, PairedDeviceActivity.class);
                    Log.d(TAG, "🚀 Intent created: " + intent.getComponent());

                    startActivity(intent);
                    Log.d(TAG, "✅ startActivity() called successfully");

                } catch (Exception e) {
                    Log.e(TAG, "❌ Error launching PairedDeviceActivity_iki", e);
                    showToast("Error opening paired devices: " + e.getMessage());
                }

            } else {
                Log.d(TAG, "⚠️ Permissions missing, requesting permissions");
                requestRequiredPermissions();
            }*/
            adManager.incrementCounter(CounterManager.CounterType.ACTIVITY);

            // Adım 2: Sayaç 5'in katı mı diye kontrol et.
            if (adManager.shouldShowEveryNCount(CounterManager.CounterType.ACTIVITY, 8)) {

                // Adım 3a: Evet, 5'in katı. Önce reklamı göster (başında yükleme ekranı ile).
                adManager.showInterstitialAdWithProgress(this, "paired_devices_click", () -> {
                    // Bu kod, reklam kapandıktan SONRA çalışır.
                    // Kullanıcıyı gitmek istediği sayfaya yönlendir.
                    if (checkAllPermissions()) {
                        startActivity(new Intent(this, PairedDeviceActivity.class));
                    } else {
                        requestRequiredPermissions();
                    }
                });

            } else {
                // Adım 3b: Hayır, 5'in katı değil. Doğrudan yeni aktiviteyi aç.
                if (checkAllPermissions()) {
                    startActivity(new Intent(this, PairedDeviceActivity.class));
                } else {
                    requestRequiredPermissions();
                }
            }
        });
        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adManager.incrementCounter(CounterManager.CounterType.ACTIVITY);

                /*if (checkAllPermissions()) {
                    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                        startActivity(new Intent(StartActivityYeni.this, SettingActivity.class));
                    } else {
                        requestBluetoothEnable();
                    }
                } else {
                    requestRequiredPermissions();
                }*/
                if (adManager.shouldShowEveryNCount(CounterManager.CounterType.ACTIVITY, 8)) {

                    // Adım 3a: Evet, 5'in katı. Önce reklamı göster (başında yükleme ekranı ile).
                    adManager.showInterstitialAdWithProgress(StartActivityYeni.this, "volume_card_click", () -> {
                        // Bu kod, reklam kapandıktan SONRA çalışır.
                        // Kullanıcıyı gitmek istediği sayfaya yönlendir.
                        if (checkAllPermissions()) {
                            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                                startActivity(new Intent(StartActivityYeni.this, SettingActivity.class));
                            } else {
                                requestBluetoothEnable();
                            }
                        } else {
                            requestRequiredPermissions();
                        }
                    });

                } else {
                    // Adım 3b: Hayır, 5'in katı değil. Doğrudan yeni aktiviteyi aç.
                    if (checkAllPermissions()) {
                        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                            startActivity(new Intent(StartActivityYeni.this, SettingActivity.class));
                        } else {
                            requestBluetoothEnable();
                        }
                    } else {
                        requestRequiredPermissions();
                    }
                }
            }
        });

        sd.setOnClickListener(v -> {
            if (checkAllPermissions()) {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    startActivity(new Intent(this, SettingActivity.class));
                } else {
                    requestBluetoothEnable();
                }
            } else {
                requestRequiredPermissions();
            }
        });
    }

    // ===== BLUETOOTH MANAGEMENT =====

    private void initializeBluetooth() {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "⚠️ Bluetooth not supported");
            showToast("Bluetooth not supported on this device");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.w(TAG, "⚠️ Bluetooth not enabled");
            updateUIForBluetoothDisabled();
            return;
        }

        Log.d(TAG, "✅ Bluetooth is available and enabled");

        // Service'i başlat (eğer daha önce başlatılmışsa)
        checkServiceStatus();
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 100);
    }

    // ===== SERVICE MANAGEMENT =====

    private void startMonitoringService() {
        try {
            Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
            startForegroundService(serviceIntent);

            isServiceRunning = true;
            updateServiceUI(true);

            Log.d(TAG, "✅ Monitoring service started");
            showToast("Battery monitoring started");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting service", e);
            notificationSwitch.setChecked(false);
            showToast("Error starting monitoring service");
        }
    }

    private void stopMonitoringService() {
        try {
            Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
            stopService(serviceIntent);

            isServiceRunning = false;
            updateServiceUI(false);

            Log.d(TAG, "⏹️ Monitoring service stopped");
            showToast("Battery monitoring stopped");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error stopping service", e);
            showToast("Error stopping monitoring service");
        }
    }

    // ===== BATTERY UPDATE HANDLING =====

    // ===== UI UPDATE METHODS =====
    private void handleBatteryUpdate(BatteryData batteryData) {
        Log.d(TAG, "🔋 Handling battery update: " + (batteryData != null ? batteryData.toString() : "null"));

        if (batteryData == null || batteryData.isDisconnected() || !batteryData.isConnected()) {
            Log.d(TAG, "📱 UI: Device disconnected");
            currentBatteryData = new BatteryData(); // Durumu sıfırla
            updateUIForDisconnectedState();
        } else if (batteryData.isAirPods() && batteryData.isConnected()) {
            Log.d(TAG, "🎧 UI: AirPods connected");
            currentBatteryData = batteryData;
            updateUIForAirPods(batteryData);
        } else if (batteryData.isGeneric() && batteryData.isConnected()) {
            Log.d(TAG, "🎵 UI: Generic device connected");
            currentBatteryData = batteryData;
            updateUIForGenericDevice(batteryData);
        } else {
            Log.d(TAG, "📱 UI: Unknown state, showing disconnected");
            currentBatteryData = new BatteryData(); // Durumu sıfırla
            updateUIForDisconnectedState();
        }
    }

    private void updateUIForDisconnectedState() {
        Log.d(TAG, "🔌 updateUIForDisconnectedState called");

        // Device title
        deviceTitle.setText("");

        // Device image - mevcut bluetooth icon kullan
      //  airpodsImage.setImageResource(R.drawable.bluetooth);

        // Show no connection section
        noConnectionSection.setVisibility(View.VISIBLE);
        batteryLevelsSection.setVisibility(View.GONE);
        genericDeviceSection.setVisibility(View.GONE);  // EKLE
        ImageView noConnectionIcon = findViewById(R.id.noConnectionIcon);
        noConnectionIcon.setImageResource(R.drawable.ic_headphones_illustration);

        Log.d(TAG, "🔌 UI updated for disconnected state - battery section HIDDEN");
    }

    private void updateUIForBluetoothDisabled() {
        deviceTitle.setText(R.string.bluetooth_disabled);
       // airpodsImage.setImageResource(R.drawable.bluetooth); // mevcut icon kullan
        noConnectionSection.setVisibility(View.VISIBLE);
        batteryLevelsSection.setVisibility(View.GONE);
    }

    private void updateUIForAirPods(BatteryData batteryData) {
        Log.d(TAG, "🎧 updateUIForAirPods called with: " + batteryData.toString());

        // Device title
        deviceTitle.setText(batteryData.getDeviceName());
        Log.d(TAG, "📱 Device title set to: " + batteryData.getDeviceName());

        // Device image
     //   airpodsImage.setImageResource(R.drawable.airpodes);

        // Show battery levels section
        noConnectionSection.setVisibility(View.GONE);
        genericDeviceSection.setVisibility(View.GONE);  // Generic'i gizle
        batteryLevelsSection.setVisibility(View.VISIBLE);  // AirPods'u göster

        // Show all sections for AirPods
        showAirPodsLayout();

        // Update battery texts
        leftBatteryText.setText(batteryData.getLeftBattery());
        rightBatteryText.setText(batteryData.getRightBattery());
        caseBatteryText.setText(batteryData.getCaseBattery());

        Log.d(TAG, "📱 Battery texts updated: L=" + batteryData.getLeftBattery() +
                " R=" + batteryData.getRightBattery() + " Case=" + batteryData.getCaseBattery());

        // Update charging icons
        updateChargingIcon(leftBatteryIcon, batteryData.isLeftCharging());
        updateChargingIcon(rightBatteryIcon, batteryData.isRightCharging());
        updateChargingIcon(caseBatteryIcon, batteryData.isCaseCharging());

        Log.d(TAG, "🎧 UI updated for AirPods: " + batteryData.getDeviceName());
    }

/*
    private void updateUIForGenericDevice(BatteryData batteryData) {
        // Device title
        deviceTitle.setText(batteryData.getDeviceName());

        // Device image - mevcut headphone icon kullan
        airpodsImage.setImageResource(R.drawable.headphone); // mevcut headphone icon'unuz

        // Show battery levels section
        noConnectionSection.setVisibility(View.GONE);
        batteryLevelsSection.setVisibility(View.VISIBLE);

        // Show only left section for generic device
        showRegularHeadphoneLayout();

        // Update battery text
        leftBatteryText.setText("Battery: " + batteryData.getSingleBattery());

        // Update charging icon
        updateChargingIcon(leftBatteryIcon, batteryData.isSingleCharging());

        Log.d(TAG, "🎵 UI updated for generic device: " + batteryData.getDeviceName());
    }
*/
private void updateUIForGenericDevice(BatteryData batteryData) {
    // Device title
    deviceTitle.setText(batteryData.getDeviceName());

    // Show no connection section hide, generic section show
    noConnectionSection.setVisibility(View.GONE);
    batteryLevelsSection.setVisibility(View.GONE);  // AirPods layout'unu gizle
    genericDeviceSection.setVisibility(View.VISIBLE);  // Generic layout'unu göster

    // Update battery text - sadece yüzde
    genericBatteryText.setText(batteryData.getSingleBattery() + "");

    Log.d(TAG, "🎵 UI updated for generic device: " + batteryData.getDeviceName());
}
    private void showAirPodsLayout() {
        leftEarbudSection.setVisibility(View.VISIBLE);
        rightEarbudSection.setVisibility(View.VISIBLE);
        caseSection.setVisibility(View.VISIBLE);

        // Reset layout params için equal weight
        resetLayoutParams(leftEarbudSection, 1.0f);
        resetLayoutParams(rightEarbudSection, 1.0f);
        resetLayoutParams(caseSection, 1.0f);
    }

    private void showRegularHeadphoneLayout() {
        leftEarbudSection.setVisibility(View.VISIBLE);
        rightEarbudSection.setVisibility(View.GONE);
        caseSection.setVisibility(View.GONE);

        // Left section full width
        resetLayoutParams(leftEarbudSection, 1.0f);
    }

    private void resetLayoutParams(View view, float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = 0;
        params.weight = weight;
        view.setLayoutParams(params);
    }

    private void updateChargingIcon(View iconContainer, boolean isCharging) {
        // 3D iconlar zaten güzel görünüyor, ek bir şey yapmaya gerek yok
        // İsteğe bağlı olarak burada icon değişiklikleri yapılabilir
    }

    private void updateServiceUI(boolean isRunning) {
        notificationSwitch.setChecked(isRunning);
        // TODO: Add other service status indicators if needed
    }

    // ===== PERMISSION MANAGEMENT =====

    private boolean checkAllPermissions() {
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "❌ Missing permission: " + permission);
                return false;
            }
        }
        return true;
    }

    private void requestRequiredPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkAllPermissions()) {
                Log.d(TAG, "✅ All permissions granted");
                initializeBluetooth();
                showToast("Permissions granted");
            } else {
                Log.w(TAG, "⚠️ Some permissions denied");
                showToast("Permissions required for Bluetooth monitoring");
            }
        }
    }

    // ===== SERVICE COMMUNICATION =====

    private void registerBatteryUpdateReceiver() {
        try {
            // Artık sadece LocalBroadcastManager kullanıyoruz.
            IntentFilter filter = new IntentFilter(UnifiedBluetoothService.ACTION_BATTERY_UPDATE);
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                    .registerReceiver(batteryUpdateReceiver, filter);
            Log.d(TAG, "✅ LOCAL broadcast receiver registered.");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error registering local battery update receiver", e);
        }
    }

    private void unregisterBatteryUpdateReceiver() {
        try {
            // Ve sadece onu siliyoruz.
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(batteryUpdateReceiver);
            Log.d(TAG, "✅ LOCAL broadcast receiver unregistered.");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error unregistering local battery update receiver", e);
        }
    }

    // ===== UTILITY METHODS =====

    private void showToast(String message) {
        runOnUiThread(() -> {
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) { // Bluetooth enable request
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "✅ Bluetooth enabled by user");
                initializeBluetooth();
            } else {
                Log.w(TAG, "⚠️ Bluetooth enable denied by user");
                showToast("Bluetooth is required for battery monitoring");
            }
        }
    }

    private boolean isBatteryLevelChanged(BatteryData old, BatteryData new_) {
        if (old.isAirPods() && new_.isAirPods()) {
            return !old.getLeftBattery().equals(new_.getLeftBattery()) ||
                    !old.getRightBattery().equals(new_.getRightBattery()) ||
                    !old.getCaseBattery().equals(new_.getCaseBattery());
        } else if (old.isGeneric() && new_.isGeneric()) {
            return !old.getSingleBattery().equals(new_.getSingleBattery());
        }
        return true; // Farklı tip veya bilinmeyen durum
    }

    private void checkServiceStatus() {
        // Service çalışıyor mu kontrol et
        isServiceRunning = isServiceRunning(UnifiedBluetoothService.class);
        updateServiceUI(isServiceRunning);

        if (isServiceRunning) {
            // Service çalışıyorsa mevcut durumu iste
            requestCurrentBatteryStatus();
        }
    }

    // 11. YENI helper method:
    private boolean isServiceRunning(Class<?> serviceClass) {
        android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // YENİ METOT: Bu metodu class'ına ekle ▼
    private void updateBottomNavSelection(boolean isHomeSelected) {
        if (isHomeSelected) {
            // Ana Sayfa'yı Aktif Yap
            infoText.setTextAppearance(R.style.BottomNavTextActive);
            infoIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue_600));
            infoText.setTextColor(ContextCompat.getColor(this, R.color.blue_600));

            // Ayarlar'ı Pasif Yap
            rateText.setTextAppearance(R.style.BottomNavTextInactive);
            rateIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary_dark));
            rateText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark));

        } else {
            // Ana Sayfa'yı Pasif Yap
            infoText.setTextAppearance(R.style.BottomNavTextInactive);
            infoIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary_dark));
            infoText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark));

            // Ayarlar'ı Aktif Yap
            rateText.setTextAppearance(R.style.BottomNavTextActive);
            rateIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue_600));
            rateText.setTextColor(ContextCompat.getColor(this, R.color.blue_600));
        }
    }

    // BU ÜÇ METODU SINIFINIZIN İÇİNE (EN ALTA) EKLEYİN

/*
    private void showAboutBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_about, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView versionText = bottomSheetView.findViewById(R.id.app_version_text);
        com.google.android.material.button.MaterialButton shareButton = bottomSheetView.findViewById(R.id.share_app_button);
        com.google.android.material.button.MaterialButton privacyButton = bottomSheetView.findViewById(R.id.privacy_policy_button);

        // Versiyon bilgisini al ve göster
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionText.setText("Versiyon " + versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pencere içindeki Paylaş butonunun tıklama olayı
        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Bu harika Bluetooth pil seviyesi uygulamasını dene!\n\n";
            shareBody += "https://play.google.com/store/apps/details?id=" + getPackageName();
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Uygulamayı Paylaş"));
            bottomSheetDialog.dismiss();
        });

        // Pencere içindeki Gizlilik Politikası butonunun tıklama olayı
        privacyButton.setOnClickListener(v -> {
            // Kendi gizlilik politikası URL'nizi buraya yapıştırın
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(browserIntent);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
*/
// showAboutBottomSheet metodunun yeni ve sade hali
private void showAboutBottomSheet() {
    new AboutBottomSheetFragment().show(getSupportFragmentManager(), "AboutBottomSheet");
}
    private void showInAppReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(reviewTask -> {
                    // Akış tamamlandı.
                });
            } else {
                // Bir sorun oluştu, B planına geç: Play Store'u aç
                openPlayStoreForRating();
            }
        });
    }

    private void openPlayStoreForRating() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}
