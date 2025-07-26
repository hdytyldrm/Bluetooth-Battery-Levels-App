package com.demo.bluetoothbatterylevel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.demo.bluetoothbatterylevel.R;
import com.demo.bluetoothbatterylevel.databinding.HistoryDeviceLayoutBinding;
import com.demo.bluetoothbatterylevel.model.History;
import com.demo.bluetoothbatterylevel.utils.Resizer;
import java.util.List;

public class HistoryDeviceAdapter extends RecyclerView.Adapter<HistoryDeviceAdapter.MyViewHolder> {
    public List<History> arrayList;
    public Context context;

    public HistoryDeviceAdapter(Context context2, List<History> list) {
        this.context = context2;
        this.arrayList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(HistoryDeviceLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.binding.name.setText(this.arrayList.get(i).getName());
        myViewHolder.binding.date.setText(this.arrayList.get(i).getDate());
        myViewHolder.binding.time.setText(this.arrayList.get(i).getTime());
        myViewHolder.binding.status.setText(this.arrayList.get(i).getStatus());
        checkBluetoothType(myViewHolder.binding.icon, this.arrayList.get(i).getName(), this.arrayList.get(myViewHolder.getAdapterPosition()).getType());
    }

    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        
        public final HistoryDeviceLayoutBinding binding;

        public MyViewHolder(HistoryDeviceLayoutBinding historyDeviceLayoutBinding) {
            super(historyDeviceLayoutBinding.getRoot());
            this.binding = historyDeviceLayoutBinding;
            Resizer.setSize(historyDeviceLayoutBinding.connectRl, 974, 206, true);
            Resizer.setSize(historyDeviceLayoutBinding.icon, 160, 160, true);
            Resizer.setMargins(historyDeviceLayoutBinding.icon, 20, 0, 20, 0);
        }
    }

    public void checkBluetoothType(ImageView imageView, String str, int i) {
        Integer valueOf = Integer.valueOf(R.drawable.headphone_small);
        if (i == 1048) {
            Glide.with(this.context).load(valueOf).into(imageView);
            Log.d("TAG", "checkBluetoothType: 1");
        } else if (i == 1028) {
            Glide.with(this.context).load(valueOf).into(imageView);
            Log.d("TAG", "checkBluetoothType: 1");
        } else if (str != null && str.contains("AirPods")) {
            Glide.with(this.context).load(Integer.valueOf(R.drawable.airpodes_small)).into(imageView);
            Log.d("TAG", "checkBluetoothType: 2");
        } else if (i == 1796) {
            Glide.with(this.context).load(Integer.valueOf(R.drawable.watch_small)).into(imageView);
            Log.d("TAG", "checkBluetoothType: 3");
        } else {
            Glide.with(this.context).load(Integer.valueOf(R.drawable.bluetooth_small)).into(imageView);
            Log.d("TAG", "checkBluetoothType: 4");
        }
    }

    public void updateData(List<History> list) {
        this.arrayList = list;
        notifyDataSetChanged();
    }
}
