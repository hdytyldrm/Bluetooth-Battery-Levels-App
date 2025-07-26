package com.hdytyldrm.batterylevel.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.TimeZone;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(setLocale(context, getSelectedLanguage(context)));
    }

    public Context setLocale(Context context, String str) {
        Locale locale = new Locale(str);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public String getSelectedLanguage(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        SpManager.initializingSharedPreference(context);
        SpManager.setIndian(timeZone.getDisplayName().equals("India Standard Time"));
        return SpManager.getLanguageCode();
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
