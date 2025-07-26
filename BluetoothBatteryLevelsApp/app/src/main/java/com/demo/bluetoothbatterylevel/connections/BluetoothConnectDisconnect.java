package com.demo.bluetoothbatterylevel.connections;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import com.demo.bluetoothbatterylevel.connections.BluetoothConnectDisconnectInterface;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

public class BluetoothConnectDisconnect {
    public static BluetoothDevice bluetoothDevice;
    private BluetoothA2dp btdp;
    private BluetoothAdapter bluetoothAdapter = null;
    private final BluetoothProfile.ServiceListener btserv = new ListenerHandle();
    public Context context;
    private BluetoothHeadset headset;
    public boolean isConnect = false;

    static class SeviceConnection implements ServiceConnection {
        BluetoothConnectDisconnectInterface btConnectDisconnectInterface;
        Context context1;

        SeviceConnection() {
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.btConnectDisconnectInterface = BluetoothConnectDisconnectInterface.Class1.method2(iBinder);
            Intent intent = new Intent();
            intent.setAction("HEADSET_INTERFACE_CONNECTED");
            this.context1.sendBroadcast(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                this.btConnectDisconnectInterface.method1(BluetoothConnectDisconnect.bluetoothDevice);
                this.btConnectDisconnectInterface = null;
            } catch (Exception unused) {
                this.btConnectDisconnectInterface = null;
            }
        }
    }

    class ListenerHandle implements BluetoothProfile.ServiceListener {
        @Override
        public void onServiceDisconnected(int i) {
        }

        ListenerHandle() {
        }

        @Override
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == 2) {
                if (BluetoothConnectDisconnect.this.isConnect) {
                    if (BluetoothConnectDisconnect.bluetoothDevice == null) {
                        BluetoothConnectDisconnect.this.method2();
                    }
                    try {
                        Method declaredMethod = BluetoothA2dp.class.getDeclaredMethod("connect", new Class[]{BluetoothDevice.class});
                        declaredMethod.setAccessible(true);
                        declaredMethod.invoke(bluetoothProfile, new Object[]{BluetoothConnectDisconnect.bluetoothDevice});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    BluetoothConnectDisconnect bluetoothConnectDisconnect = BluetoothConnectDisconnect.this;
                    bluetoothConnectDisconnect.invokeMethod(bluetoothConnectDisconnect.context, BluetoothConnectDisconnect.bluetoothDevice);
                    Intent intent = new Intent("android.settings.BLUETOOTH_SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BluetoothConnectDisconnect.this.context.startActivity(intent);
                    return;
                }
                try {
                    Method declaredMethod2 = BluetoothA2dp.class.getDeclaredMethod("disconnect", new Class[]{BluetoothDevice.class});
                    declaredMethod2.setAccessible(true);
                    declaredMethod2.invoke(bluetoothProfile, new Object[]{BluetoothConnectDisconnect.bluetoothDevice});
                } catch (Exception unused) {
                }
            } else if (i == 1) {
                if (BluetoothConnectDisconnect.bluetoothDevice == null) {
                    BluetoothConnectDisconnect.this.method1();
                }
                if (BluetoothConnectDisconnect.this.isConnect) {
                    try {
                        Method declaredMethod3 = BluetoothHeadset.class.getDeclaredMethod("connect", new Class[]{BluetoothDevice.class});
                        declaredMethod3.setAccessible(true);
                        declaredMethod3.invoke(bluetoothProfile, new Object[]{BluetoothConnectDisconnect.bluetoothDevice});
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    try {
                        Method declaredMethod4 = BluetoothHeadset.class.getDeclaredMethod("disconnect", new Class[]{BluetoothDevice.class});
                        declaredMethod4.setAccessible(true);
                        declaredMethod4.invoke(bluetoothProfile, new Object[]{BluetoothConnectDisconnect.bluetoothDevice});
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
    }

    static class btServCall implements BluetoothProfile.ServiceListener {
        final BluetoothDevice bluetoothDevice1;

        @Override
        public void onServiceDisconnected(int i) {
        }

        btServCall(BluetoothConnectDisconnect bluetoothConnectDisconnect, BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice1 = bluetoothDevice;
        }

        @Override
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothA2dp bluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
            try {
                bluetoothA2dp.getClass().getMethod("connect", new Class[]{BluetoothDevice.class}).invoke(bluetoothA2dp, new Object[]{this.bluetoothDevice1});
            } catch (Exception unused) {
            }
        }
    }

    static {
        new SeviceConnection();
    }

    public BluetoothConnectDisconnect(Context context2) {
        this.context = context2;
    }

    public void method1() {
        try {
            if (bluetoothDevice == null) {
                for (BluetoothDevice next : this.headset.getConnectedDevices()) {
                    if (this.headset.isAudioConnected(next)) {
                        bluetoothDevice = next;
                        return;
                    }
                }
            }
        } catch (Exception unused) {
        }
    }

    public void method2() {
        for (BluetoothDevice next : this.btdp.getConnectedDevices()) {
            if (this.btdp.isA2dpPlaying(next)) {
                bluetoothDevice = next;
                return;
            }
        }
    }

    public void method3(int i, String str) {
        BluetoothAdapter bluetoothAdapter2 = this.bluetoothAdapter;
        if (bluetoothAdapter2 != null) {
            bluetoothAdapter2.cancelDiscovery();
        }
        if (!str.equals("")) {
            this.isConnect = true;
            switchConnection(str, i);
        }
    }

    public void invokeMethod(Context context2, BluetoothDevice bluetoothDevice2) {
        try {
            Class<?> cls = Class.forName("android.os.ServiceManager");
            if (((IBinder) cls.getDeclaredMethod("getService", new Class[]{String.class}).invoke(cls.newInstance(), new Object[]{"bluetooth_btdp"})) == null) {
                BluetoothAdapter.getDefaultAdapter().getProfileProxy(context2, new btServCall(this, bluetoothDevice2), 2);
                return;
            }
            Class.forName("a.a.a").getDeclaredClasses()[0].getDeclaredMethod("asInterface", new Class[]{IBinder.class}).setAccessible(true);
        } catch (Exception e) {
            Toast.makeText(this.context, e.getMessage(), 1).show();
            Intent intent = new Intent("android.settings.BLUETOOTH_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
        }
    }

    public void switchConnection(String str, int i) {
        BluetoothAdapter bluetoothAdapter2;
        bluetoothDevice = null;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        if (defaultAdapter != null && defaultAdapter.isEnabled() && (bluetoothAdapter2 = this.bluetoothAdapter) != null) {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter2.getBondedDevices();
            if (bondedDevices.size() > 0) {
                Iterator<BluetoothDevice> it = bondedDevices.iterator();
                int i2 = 0;
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    BluetoothDevice next = it.next();
                    if (i2 == 0) {
                        bluetoothDevice = next;
                    }
                    String address = next.getAddress();
                    if (address != null) {
                        if (address.equals(str)) {
                            bluetoothDevice = next;
                            break;
                        } else if (i2 > 48) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                }
            }
        }
        BluetoothAdapter defaultAdapter2 = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter2;
        if (defaultAdapter2 != null) {
            if (i == 1) {
                defaultAdapter2.getProfileProxy(this.context.getApplicationContext(), this.btserv, 1);
            }
            if (i == 2) {
                this.bluetoothAdapter.getProfileProxy(this.context.getApplicationContext(), this.btserv, 2);
            }
            if (i == 3) {
                this.bluetoothAdapter.getProfileProxy(this.context.getApplicationContext(), this.btserv, 19);
            }
        }
    }

    public void manageConnection(int i, String str) {
        this.isConnect = false;
        switchConnection(str, i);
    }
}
