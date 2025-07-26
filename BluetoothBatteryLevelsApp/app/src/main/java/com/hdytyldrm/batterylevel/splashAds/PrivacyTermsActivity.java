package com.hdytyldrm.batterylevel.splashAds;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.activity.StartActivity;
import com.hdytyldrm.batterylevel.activity.StartActivityYeni;
import com.hdytyldrm.batterylevel.ads.MyApplication;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PrivacyTermsActivity extends AppCompatActivity {

    private Button accept_button;
    private CheckBox first_check, second_check;
    private TextView termtextview;
    private Toolbar toolbar;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_privacy_terms);

        activity = this;

        // View'ları ID'leri ile bul
        toolbar = findViewById(R.id.toolbar);
        first_check = findViewById(R.id.first_check);
        second_check = findViewById(R.id.second_check);
        accept_button = findViewById(R.id.accept_button);
        termtextview = findViewById(R.id.termtextview);

        // Toolbar'ı ayarla
        setSupportActionBar(toolbar);

        // Kabul et butonuna tıklandığında
        accept_button.setOnClickListener(v -> {
            if (!first_check.isChecked() || !second_check.isChecked()) {
                Toast.makeText(getApplicationContext(), "Devam etmek için seçenekleri işaretleyin", Toast.LENGTH_SHORT).show();
            } else {
                MyApplication.setuser_onetime(1);
                Intent i = new Intent(activity, StartActivityYeni.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        // Gizlilik politikası metnine tıklandığında
        termtextview.setOnClickListener(v -> {
            // Buraya kendi gizlilik politikası URL'nizi yapıştırın
            String privacyPolicyUrl = "https://www.google.com"; // Örnek URL
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(activity, "Web tarayıcısı açılamadı.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}