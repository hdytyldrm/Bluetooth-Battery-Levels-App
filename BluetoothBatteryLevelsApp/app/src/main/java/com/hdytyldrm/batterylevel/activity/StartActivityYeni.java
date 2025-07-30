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
    private boolean isNotificationEnabled = false;
    private static final String PREFS_NAME = "battery_monitor_prefs";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private boolean wasBluetoothDisabled = false;
    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                handleBluetoothStateChange(state);
            }
        }
    };
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

        initializeViews();
        adManager = UnifiedInterstitialAdManager.getInstance(this);
        initializeComponents();
        setupPermissions();
        setupUI();

        // ÖNCE preference'ı yükle
        loadNotificationPreference();
        registerBluetoothStateReceiver();

        updateBottomNavSelection(true);
        loadBannerAd();

        // Service debugging
        new Handler().postDelayed(() -> {
            ServiceDebugHelper.fullDebugReport(this);
            requestCurrentBatteryStatus();
        }, 1000);

        // Permissions kontrolü
        if (checkAllPermissions()) {
            initializeBluetooth();
            // Service'i otomatik başlat (notification preference'a göre)
            if (isNotificationEnabled) {
                startMonitoringServiceWithNotification();
            } else {
                startServiceWithoutNotification();
            }
        } else {
            requestRequiredPermissions();
        }

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
        unregisterBluetoothStateReceiver(); // Receiver'ı unregister et

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
       /* notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
        });*/
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (checkAllPermissions()) {
                    // Service'i başlat ve notification'ı etkinleştir
                    startMonitoringServiceWithNotification();
                    saveNotificationPreference(true);
                } else {
                    notificationSwitch.setChecked(false);
                    requestRequiredPermissions();
                }
            } else {
                // Service çalışmaya devam etsin ama notification'ı kapat
                disableNotificationOnly();
                saveNotificationPreference(false);
            }
        });

        // Bluetooth FAB
        bluetoothFab.setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
              //  showToast("Bluetooth not supported on this device");
                showToast(getString(R.string.toast_battery_monitoring_stopped));
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
            if (adManager.shouldShowEveryNCount(CounterManager.CounterType.ACTIVITY, 3)) {

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
                if (adManager.shouldShowEveryNCount(CounterManager.CounterType.ACTIVITY, 3)) {

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
            wasBluetoothDisabled = true; // Flag'i set et
            updateUIForDisconnectedState();
            return;
        }

        Log.d(TAG, "✅ Bluetooth is available and enabled");
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
/*
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
*/
    private void handleBatteryUpdate(BatteryData batteryData) {
        Log.d(TAG, "🔋 Handling battery update: " + (batteryData != null ? batteryData.toString() : "null"));

        if (batteryData == null) {
            Log.d(TAG, "📱 UI: Null battery data - showing disconnected");
            updateUIForDisconnectedState();
            return;
        }

        // Bluetooth kapalı kontrolü - SADECE UI güncelle, snackbar handleBluetoothStateChange'de
        if (batteryData.isBluetoothDisabled()) {
            Log.d(TAG, "📴 UI: Bluetooth disabled state");
            updateUIForDisconnectedState();
            return;
        }

        // Normal bağlantı durumu kontrolü
        if (batteryData.isDisconnected() || !batteryData.isConnected()) {
            Log.d(TAG, "📱 UI: No Apple device connected");
            currentBatteryData = new BatteryData();
            updateUIForDisconnectedState();
        } else if (batteryData.isAirPods() && batteryData.isConnected()) {
            Log.d(TAG, "🎧 UI: Apple audio device connected");
            currentBatteryData = batteryData;
            updateUIForAirPods(batteryData);
        } else {
            Log.d(TAG, "📱 UI: Unknown state, showing disconnected");
            currentBatteryData = new BatteryData();
            updateUIForDisconnectedState();
        }
    }
    // StartActivityYeni.java - handleBatteryUpdate metodunu güncelleyin:
    private void updateUIForDisconnectedState() {
        Log.d(TAG, "🔌 updateUIForDisconnectedState called");

        // Device title
        deviceTitle.setText("");

        // Show no connection section
        batteryLevelsSection.setVisibility(View.GONE);

        noConnectionSection.setVisibility(View.VISIBLE);

        // Update no connection message for Apple devices
        ImageView noConnectionIcon = findViewById(R.id.noConnectionIcon);
        noConnectionIcon.setImageResource(R.drawable.ic_headphones_illustration);

        // Optional: Update text to be Apple-specific


        Log.d(TAG, "🔌 UI updated for disconnected state - Apple device section HIDDEN");
    }
    private void updateUIForAirPods(BatteryData batteryData) {
        Log.d(TAG, "🎧 updateUIForAppleDevice called with: " + batteryData.toString());

        // Device title - DÜZELTME: Enhanced Apple device name kullan
        String originalName = batteryData.getDeviceName();
        Log.d(TAG, "📱 ORIGINAL device name from BatteryData: " + originalName);

        String enhancedName = getEnhancedAppleDeviceName(originalName);
        Log.d(TAG, "📱 ENHANCED device name: " + enhancedName);


        deviceTitle.setText(enhancedName);
        Log.d(TAG, "📱 Device title set to: " + enhancedName);

        // Show battery levels section
        noConnectionSection.setVisibility(View.GONE);
        batteryLevelsSection.setVisibility(View.VISIBLE);

        // Show all sections for Apple audio devices
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

        Log.d(TAG, "🎧 UI updated for Apple device: " + enhancedName);
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



    /*private void checkServiceStatus() {
        // Service çalışıyor mu kontrol et
        isServiceRunning = isServiceRunning(UnifiedBluetoothService.class);
        updateServiceUI(isServiceRunning);

        if (isServiceRunning) {
            // Service çalışıyorsa mevcut durumu iste
            requestCurrentBatteryStatus();
        }
    }*/
    private void checkServiceStatus() {
        // Service çalışıyor mu kontrol et ama switch'i otomatik değiştirme
        isServiceRunning = isServiceRunning(UnifiedBluetoothService.class);

        // Switch durumunu preference'tan al
        loadNotificationPreference();

        if (isServiceRunning) {
            // Service çalışıyorsa mevcut durumu iste
            requestCurrentBatteryStatus();
        } else {
            // Service çalışmıyorsa başlat (notification kapalı olarak)
            startServiceWithoutNotification();
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
    private String getEnhancedAppleDeviceName(String deviceName) {
        if (deviceName == null || deviceName.isEmpty()) {
            return "Apple Audio Device";
        }

        String lowerName = deviceName.toLowerCase();

        // AirPods model enhancement - daha detaylı
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro") && lowerName.contains("2")) return "AirPods Pro (2nd gen)";
            if (lowerName.contains("pro") && lowerName.contains("1")) return "AirPods Pro (1st gen)";
            if (lowerName.contains("pro")) return "AirPods Pro";
            if (lowerName.contains("max")) return "AirPods Max";
            if (lowerName.contains("3")) return "AirPods (3rd gen)";
            if (lowerName.contains("2")) return "AirPods (2nd gen)";
            if (lowerName.contains("1")) return "AirPods (1st gen)";
            return "AirPods";
        }

        // Beats model enhancement - daha kapsamlı
        if (lowerName.contains("beats")) {
            // Solo serisi
            if (lowerName.contains("solo pro")) return "Beats Solo Pro";
            if (lowerName.contains("solo 3")) return "Beats Solo 3";
            if (lowerName.contains("solo")) return "Beats Solo";

            // Studio serisi
            if (lowerName.contains("studio 3")) return "Beats Studio 3";
            if (lowerName.contains("studio buds")) return "Beats Studio Buds";
            if (lowerName.contains("studio")) return "Beats Studio";

            // Powerbeats serisi
            if (lowerName.contains("powerbeats pro")) return "Powerbeats Pro";
            if (lowerName.contains("powerbeats 3")) return "Powerbeats 3";
            if (lowerName.contains("powerbeats 4")) return "Powerbeats 4";
            if (lowerName.contains("powerbeats")) return "Powerbeats";

            // Diğer Beats modelleri
            if (lowerName.contains("beatsx") || lowerName.contains("beats x")) return "BeatsX";
            if (lowerName.contains("beats flex")) return "Beats Flex";
            if (lowerName.contains("beats fit pro")) return "Beats Fit Pro";

            // Genel Beats
            return "Beats";
        }

        // Eğer hiçbiri match etmezse orijinal adı geri döndür
        return deviceName;
    }

    private void loadNotificationPreference() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isNotificationEnabled = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, false);
        notificationSwitch.setChecked(isNotificationEnabled);
    }
    private void saveNotificationPreference(boolean enabled) {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
        isNotificationEnabled = enabled;
    }

    private void startMonitoringServiceWithNotification() {
        try {
            Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
            serviceIntent.putExtra("ENABLE_NOTIFICATION", true);
            startForegroundService(serviceIntent);

            isServiceRunning = true;
            isNotificationEnabled = true;
            updateServiceUI(true);

            Log.d(TAG, "✅ Monitoring service started WITH notification");
            showToast("Battery monitoring started");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting service with notification", e);
            notificationSwitch.setChecked(false);
            showToast("Error starting monitoring service");
        }
    }
    private void disableNotificationOnly() {
        try {
            // Service'e notification'ı kapat diye bildir
            Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
            serviceIntent.putExtra("ENABLE_NOTIFICATION", false);
            startForegroundService(serviceIntent);

            isNotificationEnabled = false;
            updateServiceUI(false);

            Log.d(TAG, "📴 Notification disabled, service continues");
            //showToast("Notification disabled");
            showToast(getString(R.string.toast_notification_disabled));

        } catch (Exception e) {
            Log.e(TAG, "❌ Error disabling notification", e);
            showToast("Error disabling notification");
        }
    }
    private void startServiceWithoutNotification() {
        try {
            Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
            serviceIntent.putExtra("ENABLE_NOTIFICATION", false);
            startForegroundService(serviceIntent);

            isServiceRunning = true;
            Log.d(TAG, "✅ Service started WITHOUT notification");

            // Switch durumunu değiştirme, preference'taki değeri koru

        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting service without notification", e);
        }
    }

    private void showBluetoothDisabledSnackbar() {
        com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.snackbar_bluetooth_disabled_message),
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).setAction("Enable", v -> {
            // Bluetooth ayarlarına git
            Intent bluetoothSettings = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(bluetoothSettings);
        }).show();
    }
    private void showBluetoothEnabledSnackbar() {
        com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.snackbar_bluetooth_enabled_message),
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show();
    }

    private void registerBluetoothStateReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(bluetoothStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(bluetoothStateReceiver, filter);
        }
        Log.d(TAG, "✅ Bluetooth state receiver registered");
    }

    private void unregisterBluetoothStateReceiver() {
        try {
            if (bluetoothStateReceiver != null) {
                unregisterReceiver(bluetoothStateReceiver);
                Log.d(TAG, "✅ Bluetooth state receiver unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering bluetooth state receiver", e);
        }
    }

    private void handleBluetoothStateChange(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "📴 Bluetooth turning off");
                showBluetoothDisabledSnackbar();
                wasBluetoothDisabled = true;
                break;

            case BluetoothAdapter.STATE_ON:
                Log.d(TAG, "📶 Bluetooth turned on");
                if (wasBluetoothDisabled) {
                    showBluetoothEnabledSnackbar();
                    wasBluetoothDisabled = false;
                }
                break;
        }
    }
}
