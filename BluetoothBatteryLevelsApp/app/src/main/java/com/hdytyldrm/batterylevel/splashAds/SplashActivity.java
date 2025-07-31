package com.hdytyldrm.batterylevel.splashAds;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.hdytyldrm.batterylevel.R;

import com.hdytyldrm.batterylevel.activity.StartActivityYeni;
import com.hdytyldrm.batterylevel.ads.AppOpenAdManager;
import com.hdytyldrm.batterylevel.ads.MyApplication;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIMEOUT = 3500; // 3.5 saniye
    private AppOpenAdManager adManager;
    private boolean hasNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EdgeToEdge.enable(this);

        setupStatusBar();
        setupVersionInfo(); // Versiyon bilgisini ayarlayan yeni metod

        // Elemanlara yumuşak bir giriş animasyonu ekleyelim
        applyFadeInAnimation();

        MyApplication myApplication = (MyApplication) getApplication();
        adManager = myApplication.getAppOpenAdManager();

        if (adManager != null) {
            adManager.printDebugInfo();
        }

        // Minimum splash süresini bekle ve sonra devam et
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!hasNavigated) {
                tryShowAdAndNavigate();
            }
        }, SPLASH_TIMEOUT);
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Arka plan gradient olduğu için status bar'ı transparan yapmak daha şık durabilir
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }
    }

    /**
     * Uygulamanın versiyon bilgisini alır ve ilgili TextView'a yazar.
     */
    private void setupVersionInfo() {
        TextView versionText = findViewById(R.id.versionText);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            // strings.xml'deki "Version" metnini kullanarak formatlama
            String formattedVersion = getString(R.string.text_version_info) + " " + versionName;
            versionText.setText(formattedVersion);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name not found", e);
            versionText.setText(getString(R.string.text_version_info)); // Sadece "Version" yazar
        }
    }

    /**
     * Ekrandaki ana elemanlara yavaşça belirme (fade-in) animasyonu uygular.
     */
    private void applyFadeInAnimation() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new android.view.animation.DecelerateInterpolator());
        fadeIn.setDuration(1200); // Animasyon süresi

        findViewById(R.id.logoCard).setAnimation(fadeIn);
        findViewById(R.id.appNameText).setAnimation(fadeIn);
    }

    private void tryShowAdAndNavigate() {
        if (hasNavigated) {
            Log.d(TAG, "Already navigated, skipping ad");
            return;
        }

        if (adManager != null) {
            Log.d(TAG, "Attempting to show App Open Ad...");
            adManager.showAdIfAvailable(this, this::navigateToNextScreen);
        } else {
            Log.w(TAG, "AdManager is null, navigating directly");
            navigateToNextScreen();
        }
    }

    private void navigateToNextScreen() {
        if (hasNavigated) {
            Log.d(TAG, "Already navigated, preventing duplicate navigation");
            return;
        }
        hasNavigated = true;
        Log.d(TAG, "Navigating to next screen...");

        Intent intent;
        if (MyApplication.getuser_onetime() == 0) {
            intent = new Intent(this, PrivacyTermsActivity.class);
            Log.d(TAG, "Navigating to PrivacyTermsActivity");
        } else {
            intent = new Intent(this, StartActivityYeni.class);
            Log.d(TAG, "Navigating to StartActivityYeni");
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SplashActivity destroyed");
    }
}