package com.demo.bluetoothbatterylevel.connections;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface BluetoothConnectDisconnectInterface extends IInterface {
    void method1(BluetoothDevice bluetoothDevice);

    public static abstract class Class1 extends Binder implements BluetoothConnectDisconnectInterface {

        private static class C1120bind implements BluetoothConnectDisconnectInterface {
            private final IBinder iBinder;

            C1120bind(IBinder iBinder2) {
                this.iBinder = iBinder2;
            }

            public void method1(BluetoothDevice bluetoothDevice) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.bluetooth.IBluetoothHeadset");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.iBinder.transact(2, obtain, obtain2, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
                obtain2.readException();
                obtain2.recycle();
                obtain.recycle();
            }

            public IBinder asBinder() {
                return this.iBinder;
            }
        }

        public static BluetoothConnectDisconnectInterface method2(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("android.bluetooth.IBluetoothHeadset");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof BluetoothConnectDisconnectInterface)) ? new C1120bind(iBinder) : (BluetoothConnectDisconnectInterface) queryLocalInterface;
        }
    }
}
