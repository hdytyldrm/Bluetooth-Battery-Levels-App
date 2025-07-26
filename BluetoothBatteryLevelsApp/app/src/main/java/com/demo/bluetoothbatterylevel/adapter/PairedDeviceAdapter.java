package com.demo.bluetoothbatterylevel.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.demo.bluetoothbatterylevel.R;
import com.demo.bluetoothbatterylevel.databinding.PairedDeviceLayoutBinding;
import com.demo.bluetoothbatterylevel.utils.Resizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PairedDeviceAdapter extends RecyclerView.Adapter<PairedDeviceAdapter.MyViewHolder> {
    public ArrayList<BluetoothDevice> deviceList;
    public Context context;
    private final CreationInterface creationInterface;
    private final Handler batteryHandler = new Handler(Looper.getMainLooper());

    public interface CreationInterface {
        void onConnectDisconnectClicked(BluetoothDevice bluetoothDevice);

        boolean isDeviceConnected(BluetoothDevice bluetoothDevice);
    }

    public PairedDeviceAdapter(Context context, ArrayList<BluetoothDevice> deviceList, CreationInterface creationInterface) {
        this.context = context;
        this.deviceList = deviceList;
        this.creationInterface = creationInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Yeni layout'umuzu inflate ediyoruz
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_paired_device, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        if (device == null) return;

        // Cihaz adını ayarla
       // holder.deviceName.setText(device.getName() != null ? device.getName() : "Bilinmeyen Cihaz");
        // YENİ HALİ (İsim yoksa MAC adresini gösterir)
        String deviceName = device.getName();
        if (TextUtils.isEmpty(deviceName)) {
            // İsim boş veya null ise, cihazın MAC adresini göster
            deviceName = device.getAddress();
        }
        holder.deviceName.setText(deviceName);

        // Cihaz ikonunu ayarla
        setDeviceIcon(holder.deviceIcon, device);

        // Bağlantı durumuna göre UI'ı güncelle
        boolean isConnected = creationInterface.isDeviceConnected(device);
        if (isConnected) {
            holder.deviceStatus.setText(context.getResources().getString(R.string.status_connected));
            holder.deviceStatus.setVisibility(View.VISIBLE);
            holder.connectDisconnectIcon.setImageResource(R.drawable.ic_link); // Bağlı ikonu
            holder.connectDisconnectIcon.setColorFilter(ContextCompat.getColor(context, R.color.blue_600));

            // Pil seviyesini al ve göster
            holder.batteryLevel.setVisibility(View.VISIBLE);
            getBatteryLevel(device, holder.batteryLevel);

        } else {
            holder.deviceStatus.setVisibility(View.GONE);
            holder.batteryLevel.setVisibility(View.GONE);
            holder.connectDisconnectIcon.setImageResource(R.drawable.ic_unlink); // Bağlı değil ikonu
            holder.connectDisconnectIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary_dark));
        }

        // Tıklama olayını arayüze bildir
        holder.itemView.setOnClickListener(v -> creationInterface.onConnectDisconnectClicked(device));
        holder.connectDisconnectIcon.setOnClickListener(v -> creationInterface.onConnectDisconnectClicked(device));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    // ViewHolder class'ı yeni ID'lere göre güncellendi
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceStatus;
        TextView batteryLevel;
        ImageView connectDisconnectIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceIcon = itemView.findViewById(R.id.deviceIcon);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceStatus = itemView.findViewById(R.id.deviceStatus);
            batteryLevel = itemView.findViewById(R.id.batteryLevel);
            connectDisconnectIcon = itemView.findViewById(R.id.connectDisconnectIcon);
        }
    }

    public void updateList(ArrayList<BluetoothDevice> newList) {
        this.deviceList = newList;
        notifyDataSetChanged();
    }

    private void setDeviceIcon(ImageView imageView, BluetoothDevice bluetoothDevice) {
        // Eski adapter'daki icon belirleme mantığı aynen korunuyor
        int deviceClass = bluetoothDevice.getBluetoothClass().getDeviceClass();
        String name = bluetoothDevice.getName();
        int defaultIcon = R.drawable.ic_headphones; // Genel kulaklık ikonu

        if (name != null && (name.toLowerCase().contains("airpods") || name.toLowerCase().contains("galaxy buds"))) {
            Glide.with(context).load(R.drawable.airport_left_3d).into(imageView); // Veya uygun başka bir ikon
        } else if (deviceClass == 1048 || deviceClass == 1028 || deviceClass == 1056) { // Headset, Audio/Video
            Glide.with(context).load(defaultIcon).into(imageView);
        } else if (deviceClass == 1796) { // Watch
            Glide.with(context).load(R.drawable.ic_watch).into(imageView);
        } else {
            Glide.with(context).load(R.drawable.ic_bluetooth).into(imageView); // Genel bluetooth ikonu
        }
    }

    private void getBatteryLevel(BluetoothDevice device, TextView batteryTextView) {
        try {
            Method getBatteryLevelMethod = device.getClass().getMethod("getBatteryLevel");
            int batteryLevel = (int) getBatteryLevelMethod.invoke(device);

            if (batteryLevel != -1) {
                batteryTextView.setText(batteryLevel + "%");
                // Pil seviyesine göre renk de ayarlanabilir
                if (batteryLevel < 20) {
                    batteryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_alert, 0, 0, 0);
                    batteryTextView.getCompoundDrawables()[0].setTint(ContextCompat.getColor(context, R.color.red_500));
                } else {
                    batteryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery, 0, 0, 0);
                    batteryTextView.getCompoundDrawables()[0].setTint(ContextCompat.getColor(context, R.color.green_500));
                }
            } else {
                batteryTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            batteryTextView.setVisibility(View.GONE);
        }
    }
}