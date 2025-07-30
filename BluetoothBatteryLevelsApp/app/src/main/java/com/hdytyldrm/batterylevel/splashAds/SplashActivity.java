package com.hdytyldrm.batterylevel.splashAds;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

/*
public class SplashActivity extends AppCompatActivity {

    String var;
    int check = 0;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private static final int SPLASH_DELAY = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorlight));
        }


        refreshItem();
        new Handler(Looper.getMainLooper()).postDelayed(this::goToNextScreen, SPLASH_DELAY);

    }

    private void refreshItem() {

        if(isNetworkConnected()){

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    OpenAppAds();
                }
            }, 5000);

        } else {

            final Dialog dialog = new Dialog(SplashActivity.this, R.style.DialogTheme);
            dialog.setContentView(R.layout.no_internet);
            dialog.setCancelable(false);

            CardView imgRefresh = (CardView) dialog.findViewById(R.id.refresh);
            CardView imgExit = (CardView) dialog.findViewById(R.id.exit);
            ImageView imgNoInternet = (ImageView) dialog.findViewById(R.id.img);

            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
            imgNoInternet.startAnimation(animShake);

            imgNoInternet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgNoInternet.startAnimation(animShake);
                }
            });
            imgRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshItem();
                    dialog.dismiss();
                }
            });
            imgExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                }
            });
            dialog.show();
        }

    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void OpenAppAds() {
        try {

            if (MyApplication.getuser_balance() == 0 && !MyApplication.App_Open.equals("")) {

                String app_open_ads_id = MyApplication.App_Open;

                loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);

                        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                goNext(1);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                goNext(1);
                            }
                        };
                        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                        appOpenAd.show(SplashActivity.this);

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        goNext(1);
                    }
                };

                AppOpenAd.load((Context) this, app_open_ads_id, new AdRequest.Builder().build(), 1, this.loadCallback);

            } else {
                goNext(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goNext(int i) {
        check = check + 1;
        if(check == 1){
            loadOpenApp();
        }
    }

    private void loadOpenApp() {

        //one time call & load ads
        //AdsCommon.OneTimeCall(this);

        if (MyApplication.getuser_onetime() == 0) {
            Intent i = new Intent(SplashActivity.this, PrivacyTermsActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Intent i = new Intent(SplashActivity.this, StartActivityYeni.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
    private void goToNextScreen() {
        // MyApplication sınıfındaki AppOpenAdManager, reklam hazırsa
        // bu noktada (uygulama ön plana geldiğinde) zaten göstermiş olacak.
        // Biz sadece hangi aktiviteye gideceğimizi kontrol ediyoruz.

        if (MyApplication.getuser_onetime() == 0) {
            // Kullanıcı koşulları daha önce kabul etmemiş, Gizlilik ekranına yönlendir.
            startActivity(new Intent(SplashActivity.this, PrivacyTermsActivity.class));
        } else {
            // Kullanıcı koşulları kabul etmiş, Ana Ekrana yönlendir.
            startActivity(new Intent(SplashActivity.this, StartActivityYeni.class));
        }
        finish(); // SplashActivity'yi kapat ki geri tuşuyla dönülmesin.
    }
}
*/
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIMEOUT = 3000; // 3 saniye
    private AppOpenAdManager adManager;
    private boolean hasNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setupStatusBar();

        // AppOpenAdManager'ı al
        MyApplication myApplication = (MyApplication) getApplication();
        adManager = myApplication.getAppOpenAdManager();

        // Debug bilgisi
        if (adManager != null) {
            adManager.printDebugInfo();
        }

        // Minimum splash süresini bekle, sonra reklam göstermeyi dene
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
            window.setStatusBarColor(getResources().getColor(R.color.colorlight));
        }
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
            // Kullanıcı koşulları daha önce kabul etmemiş
            intent = new Intent(this, PrivacyTermsActivity.class);
            Log.d(TAG, "Navigating to PrivacyTermsActivity");
        } else {
            // Kullanıcı koşulları kabul etmiş
            intent = new Intent(this, StartActivityYeni.class);
            Log.d(TAG, "Navigating to StartActivityYeni");
        }

        startActivity(intent);
        finish(); // SplashActivity'yi kapat
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SplashActivity destroyed");
    }


}