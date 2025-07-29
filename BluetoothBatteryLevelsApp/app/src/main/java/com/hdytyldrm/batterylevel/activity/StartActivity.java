package com.hdytyldrm.batterylevel.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.connections.BluetoothConnectDisconnect;
import com.hdytyldrm.batterylevel.database.HistoryViewModal;
import com.hdytyldrm.batterylevel.databinding.ActivityStartBinding;
import com.hdytyldrm.batterylevel.model.History;
import com.hdytyldrm.batterylevel.service.BluetoothService;
import com.hdytyldrm.batterylevel.utils.BaseActivity;
import com.hdytyldrm.batterylevel.utils.Common;
import com.hdytyldrm.batterylevel.utils.Resizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class StartActivity extends BaseActivity {
    public static int main_click_AdFlag = 0;
    public static String main_click_AdFlagOnline = "1";
    BluetoothAdapter bAdapter;
    ActivityStartBinding binding;
    int click = 0;
    public BluetoothDevice connectDevice;
    HistoryViewModal historyViewModal;
    int i = 0;
    private long mLastClickTime = 0;
    String[] permissions;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent2 = intent;
            String action = intent.getAction();
            StartActivity.this.connectDevice = (BluetoothDevice) intent2.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                if (StartActivity.this.connectDevice == null) {
                    StartActivity.this.binding.disConnectRl.setVisibility(0);
                    StartActivity.this.binding.connectRl.setVisibility(8);
                    StartActivity.this.binding.history.setVisibility(8);
                    StartActivity.this.binding.bluetoothStatus.setText(StartActivity.this.getResources().getString(R.string.bbl20));
                    return;
                }
                StartActivity.this.historyViewModal.insert(new History(StartActivity.this.connectDevice.getName(), Common.getCurrentDateWithPatten("dd/MMM/yyyy"), Common.getCurrentDateWithPatten("HH:mm"), "Connect", 0, StartActivity.this.connectDevice.getBluetoothClass().getDeviceClass()));
                StartActivity.this.binding.disConnectRl.setVisibility(8);
                StartActivity.this.binding.connectRl.setVisibility(0);
                StartActivity.this.binding.history.setVisibility(0);
                try {
                    String name = StartActivity.this.connectDevice.getName();
                    if (name.equals("") && Build.VERSION.SDK_INT >= 30) {
                        name = StartActivity.this.connectDevice.getAlias();
                    }
                    if (name.equals("")) {
                        name = StartActivity.this.connectDevice.getAddress();
                    }
                    StartActivity.this.binding.name.setText(name);
                } catch (Exception e) {
                    StartActivity.this.binding.name.setText("Unknown");
                    Log.d("TAG", "onReceive: " + e.getMessage());
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        BluetoothAdapter.getDefaultAdapter().getProfileProxy(StartActivity.this, StartActivity.this.serviceListener, 1);
                    }
                }, 3000);
            } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                StartActivity.this.binding.disConnectRl.setVisibility(0);
                StartActivity.this.binding.connectRl.setVisibility(8);
                StartActivity.this.binding.history.setVisibility(8);
                StartActivity.this.binding.bluetoothStatus.setText(StartActivity.this.getResources().getString(R.string.bbl20));
                StartActivity.this.historyViewModal.insert(new History(StartActivity.this.connectDevice.getName(), Common.getCurrentDateWithPatten("dd/MMM/yyyy"), Common.getCurrentDateWithPatten("HH:mm"), "DisConnect", 0, StartActivity.this.connectDevice.getBluetoothClass().getDeviceClass()));
            } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action) && intent2.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 10) {
                try {
                    StartActivity.this.binding.disConnectRl.setVisibility(0);
                    StartActivity.this.binding.connectRl.setVisibility(8);
                    StartActivity.this.binding.history.setVisibility(8);
                    StartActivity.this.binding.bluetoothStatus.setText(StartActivity.this.getResources().getString(R.string.bbl20));
                    StartActivity.this.historyViewModal.insert(new History(StartActivity.this.connectDevice.getName(), Common.getCurrentDateWithPatten("dd/MMM/yyyy"), Common.getCurrentDateWithPatten("HH:mm"), "DisConnect", 0, StartActivity.this.connectDevice.getBluetoothClass().getDeviceClass()));
                } catch (Exception unused) {
                }
            }
        }
    };
    BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int i) {
        }

        @Override
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            for (BluetoothDevice next : bluetoothProfile.getConnectedDevices()) {
                if (next.getBluetoothClass().getDeviceClass() == 1028 || next.getBluetoothClass().getDeviceClass() == 1048) {
                    try {
                        Method method = next.getClass().getMethod("getBatteryLevel", new Class[0]);
                        if (method != null) {
                            int intValue = (int) ((float) ((Integer) method.invoke(next, new Object[0])).intValue());
                            if (intValue == -1) {
                                StartActivity.this.binding.battery.setText(StartActivity.this.getResources().getString(R.string.bbl21));
                                StartActivity.this.binding.progressBar.setProgress(0);
                            } else {
                                StartActivity.this.binding.battery.setText(intValue + "%");
                                StartActivity.this.binding.progressBar.setProgress(intValue);
                            }
                        } else {
                            Log.d("TAG", "onServiceConnected: method is null");
                            StartActivity.this.binding.battery.setText(StartActivity.this.getResources().getString(R.string.bbl21));
                            StartActivity.this.binding.progressBar.setProgress(0);
                        }
                    } catch (IllegalAccessException | NoSuchMethodException | NullPointerException | InvocationTargetException e) {
                        e.printStackTrace();
                        StartActivity.this.binding.battery.setText(StartActivity.this.getResources().getString(R.string.bbl21));
                        StartActivity.this.binding.progressBar.setProgress(0);
                    }
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(i, bluetoothProfile);
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityStartBinding inflate = ActivityStartBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView((View) inflate.getRoot());


        //Reguler Banner Ads
        RelativeLayout admob_banner = (RelativeLayout) this.binding.regulerBannerAd.AdmobBannerFrame;
        LinearLayout adContainer = (LinearLayout) this.binding.regulerBannerAd.bannerContainer;
        FrameLayout qureka = (FrameLayout) this.binding.regulerBannerAd.qureka;
        //AdsCommon.RegulerBanner(this, admob_banner, adContainer, qureka);


        if (Build.VERSION.SDK_INT >= 33) {
            this.permissions = new String[]{"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.POST_NOTIFICATIONS"};
        } else if (Build.VERSION.SDK_INT >= 31) {
            this.permissions = new String[]{"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
        } else {
            this.permissions = new String[]{"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
        }
        this.historyViewModal = (HistoryViewModal) ViewModelProviders.of((FragmentActivity) this).get(HistoryViewModal.class);
        setSize();
        setData();
        this.bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (checkPermission(this, this.permissions)) {
            connectionStatus();
            return;
        }
        this.binding.disConnectRl.setVisibility(0);
        this.binding.connectRl.setVisibility(8);
        this.binding.history.setVisibility(8);
        this.binding.bluetoothStatus.setText(getResources().getString(R.string.bbl28));
        callPermission(this.permissions);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMyServiceRunning(BluetoothService.class)) {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.on_togal)).into(this.binding.serviceStart);
        } else {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.off_togal)).into(this.binding.serviceStart);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(this.receiver);
        } catch (Exception e) {
            Log.d("TAG", "onDestroy: " + e.getMessage());
        }
    }

    private void setData() {
        this.binding.pd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = 1;
                if (checkPermission(StartActivity.this, permissions)) {
                    nextActivity();
                } else {
                    callPermission(permissions);
                }
            }
        });
        this.binding.sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = 2;
                if (checkPermission(StartActivity.this, permissions)) {
                    nextActivity();
                } else {
                    callPermission(permissions);
                }
            }
        });
        this.binding.pds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = 3;
                if (checkPermission(StartActivity.this, permissions)) {
                    nextActivity();
                } else {
                    callPermission(permissions);
                }
            }
        });
        this.binding.serviceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = 4;
                if (isMyServiceRunning(BluetoothService.class)) {
                    stopService(new Intent(StartActivity.this, BluetoothService.class));
                    Glide.with(StartActivity.this).load(Integer.valueOf(R.drawable.off_togal)).into(binding.serviceStart);
                } else if (checkPermission(StartActivity.this, permissions)) {
                    startService(new Intent(StartActivity.this, BluetoothService.class));
                    Glide.with(StartActivity.this).load(Integer.valueOf(R.drawable.on_togal)).into(binding.serviceStart);
                } else {
                    callPermission(permissions);
                }
            }
        });

        this.binding.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //AdsCommon.InterstitialAd(StartActivity.this, intent);
            }
        });

    }

    private void setSize() {
        Resizer.getheightandwidth(this);
        Resizer.setSize(this.binding.header, 1080, 160, true);
        Resizer.setSize(this.binding.connectionDetailsRl, 974, 906, true);
        Resizer.setSize(this.binding.history, 73, 73, true);
        Resizer.setSize(this.binding.connectTypeLogo, 372, 372, true);
        Resizer.setSize(this.binding.batteryRl, 153, 72, true);
        Resizer.setSize(this.binding.battery, 139, 64, true);
        Resizer.setSize(this.binding.progressBar, 139, 64, true);
        Resizer.setSize(this.binding.serviceStart, 86, 46, true);
        Resizer.setSize(this.binding.disConnect, 533, 130, true);
        Resizer.setSize(this.binding.animation, 500, 500, true);
        Resizer.setSize(this.binding.pd, 974, 206, true);
        Resizer.setSize(this.binding.pds, 974, 206, true);
        Resizer.setSize(this.binding.sd, 974, 206, true);
        Resizer.setSize(this.binding.logo1, 100, 100, true);
        Resizer.setSize(this.binding.logo2, 100, 100, true);
        Resizer.setSize(this.binding.logo3, 100, 100, true);
        Resizer.setMargins(this.binding.battery, 4, 0, 0, 0);
        Resizer.setMargins(this.binding.progressBar, 4, 0, 0, 0);
    }

    private boolean checkPermission(Context context, String[] strArr) {
        for (String str : strArr) {
            if (ContextCompat.checkSelfPermission(context, str) != 0) {
                Log.e("TAG", "checkPermission: " + str);
                return false;
            }
        }
        return true;
    }

    public void callPermission(String[] strArr) {
        ActivityCompat.requestPermissions(this, strArr, 123);
    }

    @Override
    public void onRequestPermissionsResult(int i2, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i2, strArr, iArr);
        if (i2 != 123) {
            return;
        }
        if (checkPermission(this, strArr)) {
            connectionStatus();
            int i3 = this.click;
            if (i3 == 4) {
                startService(new Intent(this, BluetoothService.class));
                Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.on_togal)).into(this.binding.serviceStart);
            } else if (i3 == 1 || i3 == 2 || i3 == 3) {
                nextActivity();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.bbl28), 0).show();
        }
    }

    private void nextActivity() {
        int i2 = this.click;
        if (i2 == 1) {
            Intent intent = new Intent(StartActivity.this, PairedDeviceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //AdsCommon.InterstitialAd(StartActivity.this, intent);
        } else if (i2 == 2) {
            this.mLastClickTime = SystemClock.elapsedRealtime();
            BluetoothAdapter bluetoothAdapter = this.bAdapter;
            if (bluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth Not Supported", 0).show();
            } else if (!bluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 100);
                Toast.makeText(getApplicationContext(), "Bluetooth Turned ON", 0).show();
            } else {
                Intent intent = new Intent(StartActivity.this, ScanDeviceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //AdsCommon.InterstitialAd(StartActivity.this, intent);
            }
        } else if (i2 == 3 && SystemClock.elapsedRealtime() - this.mLastClickTime >= 1000) {
            Intent intent = new Intent(StartActivity.this, SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
          //  AdsCommon.InterstitialAd(StartActivity.this, intent);
        }
    }

    @Override
    public void onActivityResult(int i2, int i3, Intent intent) {
        super.onActivityResult(i2, i3, intent);
        if (i2 != 100) {
            return;
        }
        if (this.bAdapter.isEnabled()) {
            StartActivity.this.startActivity(new Intent(StartActivity.this, ScanDeviceActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            return;
        }
        Toast.makeText(this, "Please enable the bluetooth", 0).show();
    }

    public void connectionStatus() {
        BluetoothAdapter bluetoothAdapter = this.bAdapter;
        if (bluetoothAdapter != null) {
            BluetoothDevice connectDevice2 = getConnectDevice(bluetoothAdapter.getBondedDevices());
            this.connectDevice = connectDevice2;
            if (connectDevice2 == null) {
                this.binding.disConnectRl.setVisibility(0);
                this.binding.connectRl.setVisibility(8);
                this.binding.history.setVisibility(8);
                this.binding.bluetoothStatus.setText(getResources().getString(R.string.bbl20));
            } else {
                this.binding.disConnectRl.setVisibility(8);
                this.binding.connectRl.setVisibility(0);
                this.binding.history.setVisibility(0);
                String name = this.connectDevice.getName();
                if (name.equals("") && Build.VERSION.SDK_INT >= 30) {
                    name = this.connectDevice.getAlias();
                }
                if (name.equals("")) {
                    name = this.connectDevice.getAddress();
                }
                checkBluetoothType(this.binding.connectTypeLogo, this.connectDevice);
                this.binding.name.setText(name);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                        StartActivity startActivity = StartActivity.this;
                        defaultAdapter.getProfileProxy(startActivity, startActivity.serviceListener, 1);
                    }
                }, 3000);
            }
        } else {
            this.binding.disConnectRl.setVisibility(0);
            this.binding.connectRl.setVisibility(8);
            this.binding.history.setVisibility(8);
            this.binding.bluetoothStatus.setText(getResources().getString(R.string.bbl20));
        }
        this.binding.disConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BluetoothConnectDisconnect bluetoothConnectDisconnect = new BluetoothConnectDisconnect(StartActivity.this);
                    bluetoothConnectDisconnect.manageConnection(2, connectDevice.getAddress());
                    bluetoothConnectDisconnect.manageConnection(1, connectDevice.getAddress());
                } catch (Exception unused) {
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        //registerReceiver(this.receiver, intentFilter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(this.receiver, intentFilter, RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(this.receiver, intentFilter);
        }

    }

    public void checkBluetoothType(ImageView imageView, BluetoothDevice bluetoothDevice) {
        int deviceClass = bluetoothDevice.getBluetoothClass().getDeviceClass();
        String name = bluetoothDevice.getName();
        Integer valueOf = Integer.valueOf(R.drawable.headphone);
        if (deviceClass == 1048) {
            Glide.with((FragmentActivity) this).load(valueOf).into(imageView);
            Log.d("TAG", "checkBluetoothType: 1");
        } else if (deviceClass == 1028) {
            Glide.with((FragmentActivity) this).load(valueOf).into(imageView);
            Log.d("TAG", "checkBluetoothType: 1");
        } else if (name != null && name.contains("AirPods")) {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.airpodes)).into(imageView);
            Log.d("TAG", "checkBluetoothType: 2");
        } else if (deviceClass == 1796) {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.watch)).into(imageView);
            Log.d("TAG", "checkBluetoothType: 3");
        } else {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.bluetooth)).into(imageView);
            Log.d("TAG", "checkBluetoothType: 4");
        }
    }

    private boolean isMyServiceRunning(Class<?> cls) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public BluetoothDevice getConnectDevice(Set<BluetoothDevice> set) {
        if (set.size() <= 0) {
            return null;
        }
        for (BluetoothDevice next : set) {
            if (isConnected(next)) {
                return next;
            }
        }
        return null;
    }

    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        try {
            Class[] clsArr = null;
            Object[] objArr = null;
            return ((Boolean) bluetoothDevice.getClass().getMethod("isConnected", (Class[]) null).invoke(bluetoothDevice, (Object[]) null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        ExitDialog();
    }

    private void ExitDialog() {

        final Dialog dialog = new Dialog(StartActivity.this, R.style.DialogTheme);
        dialog.setContentView(R.layout.popup_exit_dialog);
        dialog.setCancelable(false);

        RelativeLayout no = (RelativeLayout) dialog.findViewById(R.id.no);
        RelativeLayout rate = (RelativeLayout) dialog.findViewById(R.id.rate);
        RelativeLayout yes = (RelativeLayout) dialog.findViewById(R.id.yes);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rateapp = getPackageName();
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + rateapp));
                startActivity(intent1);
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                System.exit(0);
                //Intent intent = new Intent(AppMainHomeActivity.this, AppThankYouActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //AdsCommon.InterstitialAd(AppMainHomeActivity.this, intent);
            }
        });

        dialog.show();
    }

}
