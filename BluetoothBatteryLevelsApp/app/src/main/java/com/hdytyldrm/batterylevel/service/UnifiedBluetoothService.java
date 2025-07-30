package com.hdytyldrm.batterylevel.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.activity.StartActivityYeni;
import com.hdytyldrm.batterylevel.detection.AirPodsDetector;
import com.hdytyldrm.batterylevel.detection.BatteryDetectionListener;
import com.hdytyldrm.batterylevel.model.BatteryData;
import com.hdytyldrm.batterylevel.widget.BatteryWidgetProvider;

import java.util.Set;

/*
public class UnifiedBluetoothService extends Service implements BatteryDetectionListener {
    private static final String TAG = "UnifiedBluetoothService";

    // Notification constants
    private static final int NOTIFICATION_ID = 115600;
    private static final String CHANNEL_ID = "bluetooth_battery_channel";
    private static final String CHANNEL_NAME = "Bluetooth Battery Monitor";

    // Broadcast actions for UI communication
    public static final String ACTION_BATTERY_UPDATE = "com.hdytyldrm.batterylevel.BATTERY_UPDATE";
    public static final String EXTRA_BATTERY_DATA = "battery_data";

    // Detection strategies
    private AirPodsDetector airPodsDetector;
    private GenericBluetoothDetector genericDetector;

    // Current state
    private BatteryData currentBatteryData;
    private BluetoothDevice currentConnectedDevice = null;

    // System components
    private NotificationManager notificationManager;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver systemReceiver;
    private BroadcastReceiver debugReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "🚀 === UnifiedBluetoothService CREATING... ===");

        try {
            // ADIM 1: ÖNCE SİSTEME SÖZÜMÜZÜ TUTALIM
            // NotificationManager'ı hemen oluştur.
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Bütün yavaş işlemlerden ÖNCE notification kanalını oluştur ve servisi ön plana taşı.
            createNotificationChannel();
            startForegroundWithInitialNotification();
            Log.d(TAG, "✅ Service has been promoted to foreground.");

            // ADIM 2: ŞİMDİ GÜVENLE DİĞER İŞLEMLERİ YAP
            // Artık zaman kısıtlamamız yok.
            initializeComponents();
            setupSystemReceiver();
            setupDebugReceiver();
            startAllDetection();

            Log.d(TAG, "🚀 === UnifiedBluetoothService STARTED SUCCESSFULLY ===");

        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR in service onCreate", e);
            stopSelf(); // Hata durumunda servisi durdur.
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "🔄 Service onStartCommand called");
        // Servis zaten çalışıyorsa ve yeniden başlatılırsa,
        // dedektörlerin aktif olduğundan emin ol.
        if (airPodsDetector == null || !airPodsDetector.isActive()) {
            startAllDetection();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "🛑 UnifiedBluetoothService stopping...");
        stopAllDetection();
        unregisterReceiver(systemReceiver);
        unregisterReceiver(debugReceiver);
        super.onDestroy();
        Log.d(TAG, "✅ UnifiedBluetoothService stopped");
    }


    // ===== LISTENER IMPLEMENTATION =====

   */
/* @Override
    public void onBatteryDataReceived(BatteryData batteryData) {
        Log.d(TAG, "🔋 Battery data received from a detector: " + batteryData.toString());
        if (batteryData.isAirPods()) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null && adapter.isEnabled()) {
                try {
                    Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
                    boolean airPodsActuallyConnected = false;

                    for (BluetoothDevice device : bondedDevices) {
                        if (device.getName() != null && device.getName().contains("AirPods")) {
                            boolean check1 = isDeviceConnectedReflection(device);
                            boolean check2 = isDeviceConnectedProfile(device);
                            boolean check3 = isDeviceConnectedState(device);

                            int trueCount = (check1 ? 1 : 0) + (check2 ? 1 : 0) + (check3 ? 1 : 0);
                            boolean isReallyConnected = trueCount >= 2;

                            if (isReallyConnected) {
                                airPodsActuallyConnected = true;
                                currentConnectedDevice = device;
                                break;
                            }
                        }
                    }

                    if (!airPodsActuallyConnected) {
                        Log.d(TAG, "📍 AirPods nearby but NOT connected - ignoring update");
                        return; // Update'i ignore et
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error checking AirPods connection", e);
                    return;
                }
            }
        }


        currentBatteryData = batteryData;
        // UI, Widget ve Bildirimi güncelle
        broadcastBatteryUpdate(batteryData);
        updateNotification(batteryData);
        updateWidgets(batteryData);
    }
*//*

   @Override
   public void onBatteryDataReceived(BatteryData batteryData) {
       Log.d(TAG, "🔋 Battery data received from a detector: " + batteryData.toString());

       if (batteryData.isAirPods()) {
           BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
           if (adapter != null && adapter.isEnabled()) {
               try {
                   Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
                   boolean airPodsActuallyConnected = false;

                   for (BluetoothDevice device : bondedDevices) {
                       if (device.getName() != null && device.getName().contains("AirPods")) {
                           boolean check1 = isDeviceConnectedReflection(device);
                           boolean check2 = isDeviceConnectedProfile(device);
                           boolean check3 = isDeviceConnectedState(device);

                           // DEBUG LOGLAR - BU SATIRLARI EKLE
                           Log.d(TAG, "🔍 Connection checks: reflection=" + check1 + ", profile=" + check2 + ", bond=" + check3);

                           boolean isReallyConnected = check1 || check2;

                           Log.d(TAG, "🔍 Final result: " + isReallyConnected);

                           if (isReallyConnected) {
                               airPodsActuallyConnected = true;
                               currentConnectedDevice = device;
                               break;
                           }
                       }
                   }

                   if (!airPodsActuallyConnected) {
                       Log.d(TAG, "📍 AirPods nearby but NOT connected - ignoring update");
                       return;
                   }
               } catch (Exception e) {
                   Log.e(TAG, "Error checking AirPods connection", e);
                   return;
               }
           }
       }

       currentBatteryData = batteryData;
       broadcastBatteryUpdate(batteryData);
       updateNotification(batteryData);
     //  updateWidgets(batteryData);
       broadcastUpdateToWidgets(batteryData);
   }
    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.d(TAG, "✅ Device connected via a detector: " + (device != null ? device.getName() : "null"));
        currentConnectedDevice = device;
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        Log.d(TAG, "❌ Disconnect event from detector for: " + (device != null ? device.getName() : "null"));

        // Null cihaz kontrolü
        if (device == null) {
            Log.w(TAG, "🔌 Null device disconnected. Resetting state just in case.");
            setDisconnectedState();
            return;
        }

        // Mevcut bağlı cihaz varsa kontrol et
        if (currentBatteryData != null && !currentBatteryData.isDisconnected()) {
            // AirPods için UUID veya isim kontrolü
            boolean isDisconnectingAirPods = DeviceDetectionRouter.detectDeviceType(device).getDetectedType() == DeviceType.AIRPODS;
            if (currentBatteryData.isAirPods() && isDisconnectingAirPods) {
                Log.d(TAG, "🔌 AirPods disconnect confirmed. Resetting state.");
                setDisconnectedState();
                return;
            }

            // Genel cihazlar için MAC adresi kontrolü
            String deviceAddress = device.getAddress();
            String currentDeviceAddress = (currentConnectedDevice != null) ? currentConnectedDevice.getAddress() : null;
            if (currentDeviceAddress != null && currentDeviceAddress.equals(deviceAddress)) {
                Log.d(TAG, "🔌 Generic device disconnect confirmed by address. Resetting state.");
                setDisconnectedState();
                return;
            }
        }

        Log.w(TAG, "🔌 Disconnect event for an untracked or irrelevant device received. Ignoring.");
    }

    @Override
    public void onDetectionError(String error, Exception exception) {
        Log.e(TAG, "🚨 Detection error from a detector: " + error, exception);
    }

    @Override
    public void onDetectionStatusChanged(String status) {
        Log.d(TAG, "📊 Detection status from a detector: " + status);
    }

    // ===== PRIVATE HELPER METHODS =====

    private void initializeComponents() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        currentBatteryData = new BatteryData(); // Başlangıçta "Disconnected"

        // Dedektörleri oluştur ve listener olarak bu servisi ata.
        airPodsDetector = new AirPodsDetector(this);
        airPodsDetector.setBatteryListener(this);

        genericDetector = new GenericBluetoothDetector(this);
        genericDetector.setBatteryListener(this);
    }

    private void setupSystemReceiver() {
        // Bu receiver artık sadece BT açılıp kapanmasını dinleyecek.
        systemReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    handleBluetoothStateChange(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR));
                } else if ("com.hdytyldrm.batterylevel.REQUEST_BATTERY_STATUS".equals(action)) {
                    Log.d(TAG, "UI'dan son durum isteği alındı.");
                    if (currentBatteryData != null) {
                        broadcastBatteryUpdate(currentBatteryData);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction("com.hdytyldrm.batterylevel.REQUEST_BATTERY_STATUS");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(systemReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(systemReceiver, filter);
        }
    }

    private void setupDebugReceiver() {
        debugReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Bu metot test ve debug için, olduğu gibi kalabilir.
            }
        };
        IntentFilter debugFilter = new IntentFilter();
        debugFilter.addAction("com.hdytyldrm.batterylevel.DEBUG_SERVICE");
        debugFilter.addAction("com.hdytyldrm.batterylevel.TEST_SERVICE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(debugReceiver, debugFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(debugReceiver, debugFilter);
        }
    }

    private void startAllDetection() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.w(TAG, "⚠️ Bluetooth not enabled, cannot start detection");
            return;
        }
        try {
            airPodsDetector.startDetection();
            genericDetector.startDetection();
            Log.d(TAG, "✅ All detection strategies started");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting detection", e);
        }
    }

    private void stopAllDetection() {
        try {
            if (airPodsDetector != null) airPodsDetector.stopDetection();
            if (genericDetector != null) genericDetector.stopDetection();
            Log.d(TAG, "✅ All detection strategies stopped");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error stopping detection", e);
        }
    }

    private void handleBluetoothStateChange(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "📴 Bluetooth turning off");
                stopAllDetection();
                setDisconnectedState();
                break;
            case BluetoothAdapter.STATE_ON:
                Log.d(TAG, "📶 Bluetooth turned on");
                startAllDetection();
                break;
        }
    }

    private void setDisconnectedState() {
        Log.d(TAG, "📴 Setting disconnected state");
        currentConnectedDevice = null;
        currentBatteryData = new BatteryData(); // Disconnected state
        // UI'ı güncelle
        broadcastBatteryUpdate(currentBatteryData);
        updateNotification(currentBatteryData);
       // updateWidgets(currentBatteryData);
        broadcastUpdateToWidgets(currentBatteryData);
    }
// UnifiedBluetoothService.java -> broadcastBatteryUpdate metodunu bununla değiştirin.

    private void broadcastBatteryUpdate(BatteryData batteryData) {
        try {
            Intent intent = new Intent(ACTION_BATTERY_UPDATE);
            intent.putExtra(EXTRA_BATTERY_DATA, batteryData);

            // Bilgiyi SADECE uygulama içi yerel yayın ile gönder. Bu en güvenli yöntemdir.
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            Log.d(TAG, "📡 Battery update broadcasted via LOCAL broadcast: " + batteryData.toString());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error broadcasting local battery update", e);
        }
    }
*/
/*
    private void updateWidgets(BatteryData batteryData) {
        try {
            BatteryWidgetProvider.updateWidgets(this, batteryData);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating widgets", e);
        }
    }*//*


    // ===== NOTIFICATION SYSTEM =====

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows battery levels of connected Bluetooth devices");
            channel.setSound(null, null);
            channel.enableVibration(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundWithInitialNotification() {
        Notification notification = createNotification(new BatteryData()); // Başlangıç bildirimi
        startForeground(NOTIFICATION_ID, notification);
    }

    private void updateNotification(BatteryData batteryData) {
        if (notificationManager != null) {
            Notification notification = createNotification(batteryData);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

// UnifiedBluetoothService.java -> createNotification metodunu bununla değiştirin.

    // UnifiedBluetoothService.java -> createNotification metodunu bununla değiştirin.

*/
/*
    private Notification createNotification(BatteryData batteryData) {
        Intent mainIntent = new Intent(this, StartActivityYeni.class);
        // PendingIntent için FLAG_IMMUTABLE modern Android versiyonları için gereklidir.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.notification_layout);

        // Varsayılan değerleri ayarla
        String deviceName = "No Device Connected";
        String batteryInfo = "Connect your headphones";
        int deviceIcon = R.drawable.bluetooth;

        if (batteryData != null && !batteryData.isDisconnected()) {
            deviceName = batteryData.getDeviceName() != null ? batteryData.getDeviceName() : "Unknown Device";

            if (batteryData.isAirPods()) {
                deviceIcon = R.drawable.airpodes;
                batteryInfo = String.format("L:%s R:%s Case:%s",
                        batteryData.getLeftBattery(),
                        batteryData.getRightBattery(),
                        batteryData.getCaseBattery());
            } else if (batteryData.isGeneric()) {
                deviceIcon = R.drawable.headphone;
                // singleBattery null veya boş değilse göster, değilse "Connected" göster
                String singleLevel = batteryData.getSingleBattery();
                batteryInfo = (singleLevel != null && !singleLevel.isEmpty()) ? "Battery: " + singleLevel : "Connected";
            }
        }

        // Her durumda TextView'leri set et
        customView.setTextViewText(R.id.deviceName, deviceName);
        customView.setTextViewText(R.id.batteryInfo, batteryInfo);
        customView.setImageViewResource(R.id.deviceIcon, deviceIcon);

        // Notifikasyonun kendisini de güncelleyelim
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setCustomContentView(customView)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Büyük görünüm için de aynı customView'ı kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomBigContentView(customView);
        }

        return builder.build();
    }
*//*



    private Notification createNotification(BatteryData batteryData) {
        Intent mainIntent = new Intent(this, StartActivityYeni.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Sizin gönderdiğiniz layout'u kullanıyoruz
        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        int deviceIconRes; // İkonu tutacak değişken

        if (batteryData != null && batteryData.isConnected()) {
            // --- CİHAZ BAĞLI İSE ---
            customView.setTextViewText(R.id.notification_device_name, batteryData.getDeviceName() != null ? batteryData.getDeviceName() : "Bilinmeyen Cihaz");
            customView.setViewVisibility(R.id.notification_no_connection_text, View.GONE);

            if (batteryData.isAirPods()) {
                // Cihaz AirPods ise...
                deviceIconRes = R.drawable.airpodes; // AirPods ikonu
                customView.setViewVisibility(R.id.notification_airpods_layout, View.VISIBLE);
                customView.setViewVisibility(R.id.notification_generic_battery, View.GONE);

                customView.setTextViewText(R.id.notification_left_pod, getResources().getString(R.string.battery_level_left_earbud) +": "+ batteryData.getLeftBattery());
                customView.setTextViewText(R.id.notification_right_pod, getResources().getString(R.string.battery_level_right_earbud) +": " +batteryData.getRightBattery());
                customView.setTextViewText(R.id.notification_case, getResources().getString(R.string.battery_level_case) +": "+batteryData.getCaseBattery());
            } else {
                // Cihaz standart bir kulaklık ise...
                deviceIconRes = R.drawable.headphone; // Genel kulaklık ikonu
                customView.setViewVisibility(R.id.notification_airpods_layout, View.GONE);
                customView.setViewVisibility(R.id.notification_generic_battery, View.VISIBLE);

//                String singleLevel = batteryData.getSingleBattery();
//                String batteryInfo = (singleLevel != null && !singleLevel.isEmpty()) ? "Pil: " + singleLevel : "Bağlandı";
//                customView.setTextViewText(R.id.notification_generic_battery, batteryInfo);
                String singleLevel = batteryData.getSingleBattery();
                if (singleLevel != null && !singleLevel.isEmpty()) {
                    // Pil seviyesi varsa, "Pil: %s" formatını kullan
                    String batteryInfo = getString(R.string.battery_percentage_template, singleLevel);
                    customView.setTextViewText(R.id.notification_generic_battery, batteryInfo);
                } else {
                    // Pil seviyesi yoksa, "Bağlandı" metnini kullan
                    customView.setTextViewText(R.id.notification_generic_battery,getResources().getString(R.string.status_connected));
                }
            }
        } else {
            // --- CİHAZ BAĞLI DEĞİLSE ---
            deviceIconRes = R.drawable.ic_headphones_illustration; // Bağlantı yok ikonu
            customView.setTextViewText(R.id.notification_device_name, "Bağlantı Yok");
            customView.setViewVisibility(R.id.notification_no_connection_text, View.VISIBLE);
            customView.setViewVisibility(R.id.notification_airpods_layout, View.GONE);
            customView.setViewVisibility(R.id.notification_generic_battery, View.GONE);
        }

        // Belirlenen ikonu ImageView'a ata
        customView.setImageViewResource(R.id.notification_device_icon, deviceIconRes);

        // Bildirimi oluştur
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_bluetooth)
                .setCustomContentView(customView)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // CONNECTION CHECK METHODS - ESKİ KODDAN
    private boolean isDeviceConnectedReflection(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("isConnected");
            return (Boolean) method.invoke(device);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDeviceConnectedProfile(BluetoothDevice device) {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            int a2dpState = adapter.getProfileConnectionState(BluetoothProfile.A2DP);
            int headsetState = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            return (a2dpState == BluetoothProfile.STATE_CONNECTED) ||
                    (headsetState == BluetoothProfile.STATE_CONNECTED);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDeviceConnectedState(BluetoothDevice device) {
        try {
            return device.getBondState() == BluetoothDevice.BOND_BONDED;
        } catch (Exception e) {
            return false;
        }
    }

    private void broadcastUpdateToWidgets(BatteryData batteryData) {
        try {
            Intent intent = new Intent(this, BatteryWidgetProvider.class);
            intent.setAction(BatteryWidgetProvider.ACTION_WIDGET_BATTERY_UPDATE);
            intent.putExtra(BatteryWidgetProvider.EXTRA_BATTERY_DATA, batteryData);
            sendBroadcast(intent);
            Log.d(TAG, "📡 Battery update broadcasted to WIDGETS: " + batteryData.toString());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error broadcasting widget update", e);
        }
    }
}*/
/**
 * Unified Bluetooth Service - Apple Devices Only
 * Focuses exclusively on AirPods and Beats devices
 * Removed generic Bluetooth support for Play Store compliance
 */
/**
 * Unified Bluetooth Service - Apple Devices Only
 * Focuses exclusively on AirPods and Beats devices
 * Removed generic Bluetooth support for Play Store compliance
 */
public class UnifiedBluetoothService extends Service implements BatteryDetectionListener {
    private static final String TAG = "UnifiedBluetoothService";

    // Notification constants
    private static final int NOTIFICATION_ID = 115600;
    private static final String CHANNEL_ID = "bluetooth_battery_channel";
    private static final String CHANNEL_NAME = "Apple Device Battery Monitor";

    // Broadcast actions for UI communication
    public static final String ACTION_BATTERY_UPDATE = "com.hdytyldrm.batterylevel.BATTERY_UPDATE";
    public static final String EXTRA_BATTERY_DATA = "battery_data";

    // Detection strategy - Apple devices only
    private AirPodsDetector airPodsDetector;

    // Current state
    private BatteryData currentBatteryData;
    private BluetoothDevice currentConnectedDevice = null;

    // System components
    private NotificationManager notificationManager;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver systemReceiver;
    private BroadcastReceiver debugReceiver;
    private boolean isNotificationEnabled = true; // Varsayılan true


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "🚀 === Apple Device Service CREATING... ===");

        try {
            // STEP 1: Immediate foreground promotion
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel();
            startForegroundWithInitialNotification();
            Log.d(TAG, "✅ Service promoted to foreground.");
            // ÖNCE minimal notification ile foreground'a geç
            startForegroundWithMinimalNotification();

            // STEP 2: Initialize components safely
            initializeComponents();
            setupSystemReceiver();
            setupDebugReceiver();
            startAppleDeviceDetection();

            Log.d(TAG, "🚀 === Apple Device Service STARTED SUCCESSFULLY ===");

        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR in service onCreate", e);
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "🔄 Service onStartCommand called");

        Log.d(TAG, "🔄 Service onStartCommand called");

        // Notification durumunu kontrol et
        // Notification durumunu kontrol et
        if (intent != null && intent.hasExtra("ENABLE_NOTIFICATION")) {
            boolean enableNotification = intent.getBooleanExtra("ENABLE_NOTIFICATION", true);
            isNotificationEnabled = enableNotification;
            Log.d(TAG, "📱 Notification enabled: " + isNotificationEnabled);

            // Notification'ı güncelle
            updateNotificationVisibility();
        }

        // Dedektörlerin aktif olduğundan emin ol
        if (airPodsDetector == null || !airPodsDetector.isActive()) {
            startAppleDeviceDetection();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "🛑 Apple Device Service stopping...");
        stopAppleDeviceDetection();

        try {
            if (systemReceiver != null) unregisterReceiver(systemReceiver);
            if (debugReceiver != null) unregisterReceiver(debugReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering receivers", e);
        }

        super.onDestroy();
        Log.d(TAG, "✅ Apple Device Service stopped");
    }

    // ===== BATTERY DETECTION LISTENER IMPLEMENTATION =====

    @Override
    public void onBatteryDataReceived(BatteryData batteryData) {
        Log.d(TAG, "🔋 Apple device battery data received: " + batteryData.toString());

        // Enhanced Apple device connection verification
        if (batteryData.isAirPods() && !verifyAppleDeviceConnection(batteryData)) {
            Log.d(TAG, "📍 Apple device nearby but NOT connected - ignoring update");
            return;
        }

        currentBatteryData = batteryData;

        // UI'ı HER ZAMAN güncelle (notification durumuna bakma)
        broadcastBatteryUpdate(batteryData);

        // Widget'ı HER ZAMAN güncelle
        broadcastUpdateToWidgets(batteryData);

        // Notification'ı sadece enabled ise güncelle
        if (isNotificationEnabled) {
            updateNotification(batteryData);
        }

        Log.d(TAG, "🔋 Battery data processed - UI and widgets updated");
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.d(TAG, "✅ Apple device connected: " + (device != null ? device.getName() : "null"));
        currentConnectedDevice = device;
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        Log.d(TAG, "❌ Apple device disconnect event: " + (device != null ? device.getName() : "null"));

        // DÜZELTME: Her disconnect event'te state'i sıfırla
        Log.d(TAG, "🔌 Apple device disconnected. Resetting state immediately.");
        setDisconnectedState(); // Bu metod artık UI'ı her zaman güncelleyecek
    }

    @Override
    public void onDetectionError(String error, Exception exception) {
        Log.e(TAG, "🚨 Apple device detection error: " + error, exception);
    }

    @Override
    public void onDetectionStatusChanged(String status) {
        Log.d(TAG, "📊 Apple device detection status: " + status);
    }

    // ===== PRIVATE HELPER METHODS =====

    private void initializeComponents() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        currentBatteryData = new BatteryData(); // Initially disconnected

        // Initialize Apple device detector
        airPodsDetector = new AirPodsDetector(this);
        airPodsDetector.setBatteryListener(this);
    }

    private void setupSystemReceiver() {
        systemReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    handleBluetoothStateChange(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR));
                } else if ("com.hdytyldrm.batterylevel.REQUEST_BATTERY_STATUS".equals(action)) {
                    Log.d(TAG, "UI requested current battery status.");
                    if (currentBatteryData != null) {
                        broadcastBatteryUpdate(currentBatteryData);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction("com.hdytyldrm.batterylevel.REQUEST_BATTERY_STATUS");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(systemReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(systemReceiver, filter);
        }
    }

    private void setupDebugReceiver() {
        debugReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Debug command received: " + intent.getAction());
            }
        };

        IntentFilter debugFilter = new IntentFilter();
        debugFilter.addAction("com.hdytyldrm.batterylevel.DEBUG_SERVICE");
        debugFilter.addAction("com.hdytyldrm.batterylevel.TEST_SERVICE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(debugReceiver, debugFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(debugReceiver, debugFilter);
        }
    }

    private void startAppleDeviceDetection() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.w(TAG, "⚠️ Bluetooth not enabled, cannot start Apple device detection");
            return;
        }

        try {
            airPodsDetector.startDetection();
            Log.d(TAG, "✅ Apple device detection started");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting Apple device detection", e);
        }
    }

    private void stopAppleDeviceDetection() {
        try {
            if (airPodsDetector != null) {
                airPodsDetector.stopDetection();
            }
            Log.d(TAG, "✅ Apple device detection stopped");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error stopping Apple device detection", e);
        }
    }

    private void handleBluetoothStateChange(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "📴 Bluetooth turning off");
                stopAppleDeviceDetection();
                currentBatteryData = new BatteryData();
                currentBatteryData.setBluetoothDisabled(true); // Bu metodu BatteryData'ya eklemeniz gerekebilir
               // setDisconnectedState();
                broadcastBatteryUpdate(currentBatteryData);
                broadcastUpdateToWidgets(currentBatteryData);

                if (isNotificationEnabled) {
                    updateNotification(currentBatteryData);
                }
                break;
            case BluetoothAdapter.STATE_ON:
                Log.d(TAG, "📶 Bluetooth turned on");
                startAppleDeviceDetection();
                // Normal disconnected state'e dön
                setDisconnectedState();
                break;
        }
    }

    private void setDisconnectedState() {
        Log.d(TAG, "📴 Setting disconnected state");
        currentConnectedDevice = null;
        currentBatteryData = new BatteryData();

        // UI'ı HER ZAMAN güncelle (notification durumuna bakma)
        broadcastBatteryUpdate(currentBatteryData);

        // Widget'ı HER ZAMAN güncelle
        broadcastUpdateToWidgets(currentBatteryData);

        // Notification'ı sadece enabled ise güncelle
        if (isNotificationEnabled) {
            updateNotification(currentBatteryData);
        }

        Log.d(TAG, "📴 Disconnected state set - UI and widgets updated");
    }

    /**
     * Enhanced Apple device connection verification
     * Uses safer methods without reflection
     */
    private boolean verifyAppleDeviceConnection(BatteryData batteryData) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }

        try {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

            for (BluetoothDevice device : bondedDevices) {
                if (isAppleDevice(device)) {
                    // Use safer connection check without reflection
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        currentConnectedDevice = device;
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error verifying Apple device connection", e);
        }

        return false;
    }

    /**
     * Check if device is an Apple device - Enhanced detection
     */
    private boolean isAppleDevice(BluetoothDevice device) {
        if (device == null) return false;

        String name = device.getName();
        if (name == null) return false;

        String lowerName = name.toLowerCase();

        // AirPods models
        if (lowerName.contains("airpods")) return true;

        // Beats models
        if (lowerName.contains("beats solo") ||
                lowerName.contains("beats studio") ||
                lowerName.contains("powerbeats pro") ||
                lowerName.contains("powerbeats 3") ||
                lowerName.contains("beats x") ||
                lowerName.contains("beatsx") ||
                lowerName.contains("beats flex")) return true;

        // Additional Apple audio devices
        if (lowerName.contains("beats") &&
                (lowerName.contains("pro") ||
                        lowerName.contains("solo") ||
                        lowerName.contains("studio"))) return true;

        return false;
    }

    /**
     * Check if disconnected device is our current Apple device
     */
    private boolean isCurrentAppleDevice(BluetoothDevice device) {
        if (currentConnectedDevice == null || device == null) return false;
        return currentConnectedDevice.getAddress().equals(device.getAddress());
    }

    private void broadcastBatteryUpdate(BatteryData batteryData) {
        try {
            Intent intent = new Intent(ACTION_BATTERY_UPDATE);
            intent.putExtra(EXTRA_BATTERY_DATA, batteryData);

            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(intent);

            Log.d(TAG, "📡 Battery update broadcasted via LOCAL broadcast: " + batteryData.toString());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error broadcasting local battery update", e);
        }
    }

    private void broadcastUpdateToWidgets(BatteryData batteryData) {
        try {
            Intent intent = new Intent(this, BatteryWidgetProvider.class);
            intent.setAction(BatteryWidgetProvider.ACTION_WIDGET_BATTERY_UPDATE);
            intent.putExtra(BatteryWidgetProvider.EXTRA_BATTERY_DATA, batteryData);
            sendBroadcast(intent);
            Log.d(TAG, "📡 Battery update broadcasted to WIDGETS: " + batteryData.toString());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error broadcasting widget update", e);
        }
    }

    // ===== NOTIFICATION SYSTEM =====

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows battery levels of Apple devices (AirPods, Beats)");
            channel.setSound(null, null);
            channel.enableVibration(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

   /* private void startForegroundWithInitialNotification() {
        Notification notification = createNotification(new BatteryData());
        startForeground(NOTIFICATION_ID, notification);
    }*/
   private void startForegroundWithInitialNotification() {
       if (isNotificationEnabled) {
           Notification notification = createNotification(new BatteryData());
           startForeground(NOTIFICATION_ID, notification);
           Log.d(TAG, "📱 Started foreground with initial notification");
       } else {
           // Gizli bir minimal notification ile foreground'a geç
           Notification minimalNotification = createMinimalNotification();
           startForeground(NOTIFICATION_ID, minimalNotification);
           // Hemen notification'ı gizle
           stopForeground(true);
           Log.d(TAG, "📴 Started foreground then immediately hid notification");
       }
   }
    private Notification createMinimalNotification() {
        Intent mainIntent = new Intent(this, StartActivityYeni.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_bluetooth)
                .setContentTitle("Battery Monitor")
                .setContentText("Running in background")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .build();
    }
   /* private void updateNotification(BatteryData batteryData) {
        if (notificationManager != null) {
            Notification notification = createNotification(batteryData);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }*/
   private void updateNotification(BatteryData batteryData) {
       if (!isNotificationEnabled) {
           Log.d(TAG, "📴 Notification disabled, keeping minimal notification");
           return;
       }

       if (notificationManager != null) {
           Notification notification = createNotification(batteryData);
           notificationManager.notify(NOTIFICATION_ID, notification);
           Log.d(TAG, "📱 Battery notification updated");
       }
   }
    private Notification createNotification(BatteryData batteryData) {
        Intent mainIntent = new Intent(this, StartActivityYeni.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        int deviceIconRes;

        if (batteryData != null && batteryData.isConnected()) {
            // Apple device connected
            customView.setTextViewText(R.id.notification_device_name,
                    batteryData.getDeviceName() != null ? batteryData.getDeviceName() : "Unknown Apple Device");
            customView.setViewVisibility(R.id.notification_no_connection_text, View.GONE);

            if (batteryData.isAirPods()) {
                deviceIconRes = R.drawable.airpodes;
                customView.setViewVisibility(R.id.notification_airpods_layout, View.VISIBLE);
                customView.setViewVisibility(R.id.notification_generic_battery, View.GONE);

                customView.setTextViewText(R.id.notification_left_pod,
                        getResources().getString(R.string.battery_level_left_earbud) + ": " + batteryData.getLeftBattery());
                customView.setTextViewText(R.id.notification_right_pod,
                        getResources().getString(R.string.battery_level_right_earbud) + ": " + batteryData.getRightBattery());
                customView.setTextViewText(R.id.notification_case,
                        getResources().getString(R.string.battery_level_case) + ": " + batteryData.getCaseBattery());
            } else {
                // This shouldn't happen in Apple-only mode, but keeping for safety
                deviceIconRes = R.drawable.headphone;
                customView.setViewVisibility(R.id.notification_airpods_layout, View.GONE);
                customView.setViewVisibility(R.id.notification_generic_battery, View.VISIBLE);
                customView.setTextViewText(R.id.notification_generic_battery,
                        getResources().getString(R.string.status_connected));
            }
        } else {
            // No Apple device connected
            deviceIconRes = R.drawable.ic_headphones_illustration;
            customView.setTextViewText(R.id.notification_device_name,
                    getResources().getString(R.string.notification_no_device));
            customView.setViewVisibility(R.id.notification_no_connection_text, View.VISIBLE);
            customView.setViewVisibility(R.id.notification_airpods_layout, View.GONE);
            customView.setViewVisibility(R.id.notification_generic_battery, View.GONE);
        }

        customView.setImageViewResource(R.id.notification_device_icon, deviceIconRes);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_bluetooth)
                .setCustomContentView(customView)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateNotificationVisibility() {
        if (isNotificationEnabled) {
            // Notification'ı göster/güncelle
            if (currentBatteryData != null) {
                updateNotification(currentBatteryData);
            } else {
                // Varsayılan notification göster
                Notification notification = createNotification(new BatteryData());
                if (notificationManager != null) {
                    notificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
            Log.d(TAG, "📱 Notification enabled and updated");
        } else {
            // Minimal notification'a geri dön
            Notification minimalNotification = createMinimalNotification();
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, minimalNotification);
            }
            Log.d(TAG, "📱 Switched to minimal notification");
        }
    }

    private void hideNotification() {
        if (notificationManager != null) {
            // Foreground service'ten çık ama service'i durdurma
            stopForeground(true);
            Log.d(TAG, "📴 Notification hidden via stopForeground");
        }
    }

    private void startForegroundWithMinimalNotification() {
        // Her durumda minimal notification ile foreground'a geç
        Notification minimalNotification = createMinimalNotification();
        startForeground(NOTIFICATION_ID, minimalNotification);
        Log.d(TAG, "📱 Started foreground with minimal notification");
    }
}