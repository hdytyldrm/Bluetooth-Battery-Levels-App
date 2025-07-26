package com.hdytyldrm.batterylevel.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.hdytyldrm.batterylevel.adapter.HistoryDeviceAdapter;
import com.hdytyldrm.batterylevel.database.HistoryViewModal;
import com.hdytyldrm.batterylevel.databinding.ActivityHistoryBinding;
import com.hdytyldrm.batterylevel.model.History;
import com.hdytyldrm.batterylevel.utils.BaseActivity;
import com.hdytyldrm.batterylevel.utils.Resizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    List<History> arrayList; // = new ArrayList();
    ActivityHistoryBinding binding;
    HistoryDeviceAdapter historyDeviceAdapter;
    HistoryViewModal historyViewModal;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityHistoryBinding inflate = ActivityHistoryBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView((View) inflate.getRoot());


        //Reguler Banner Ads
        RelativeLayout admob_banner = (RelativeLayout) this.binding.regulerBannerAd.AdmobBannerFrame;
        LinearLayout adContainer = (LinearLayout) this.binding.regulerBannerAd.bannerContainer;
        FrameLayout qureka = (FrameLayout) this.binding.regulerBannerAd.qureka;
        //AdsCommon.RegulerBanner(this, admob_banner, adContainer, qureka);


        setSize();
        setData();
    }

    private void setSize() {
        Resizer.getheightandwidth(this);
        Resizer.setSize(this.binding.header, 1080, 170, true);
        Resizer.setSize(this.binding.back, 80, 110, true);
        Resizer.setSize(this.binding.delete, 50, 60, true);
        Resizer.setSize(this.binding.nomsg, 375, 400, true);
        this.binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setData() {
        this.arrayList = new ArrayList();
        this.historyViewModal = (HistoryViewModal) ViewModelProviders.of((FragmentActivity) this).get(HistoryViewModal.class);
        this.historyDeviceAdapter = new HistoryDeviceAdapter(this, this.arrayList);
        this.binding.listRV.setAdapter(this.historyDeviceAdapter);
        this.historyViewModal.getAllData().observe(this, new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> list) {
                if (list == null || list.size() <= 0) {
                    HistoryActivity.this.binding.listRV.setVisibility(8);
                    HistoryActivity.this.binding.noData.setVisibility(0);
                    return;
                }
                Collections.reverse(list);
                HistoryActivity.this.historyDeviceAdapter.updateData(list);
                HistoryActivity.this.binding.noData.setVisibility(8);
                HistoryActivity.this.binding.listRV.setVisibility(0);
            }
        });
        this.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyViewModal.deleteAllData();
            }
        });

    }

}
