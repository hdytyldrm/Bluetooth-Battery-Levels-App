package com.hdytyldrm.batterylevel.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Date;

/*
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    // OneSignal ID'sini boş bıraktık (kullanmıyoruz)
    private static final String ONESIGNAL_APP_ID = "";

    public static SharedPreferences sharedPreferencesInApp;
    public static SharedPreferences.Editor editorInApp;

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    //test ads id admob
    public static String AdMob_Banner1 = "ca-app-pub-3940256099942544/6300978111";
    public static String AdMob_Int1 = "ca-app-pub-3940256099942544/1033173712";
    public static String AdMob_Int2 = "ca-app-pub-3940256099942544/1033173712";
    public static String AdMob_NativeAdvance1 = "ca-app-pub-3940256099942544/2247696110";
    public static String AdMob_NativeAdvance2 = "ca-app-pub-3940256099942544/2247696110";
    public static String App_Open = "ca-app-pub-3940256099942544/9257395921";

    //test ads id fb
    public static String FbBanner = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static String FbInter = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static String Fbnative = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static String FbNativeB = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

    public static String MAX_Banner = "";
    public static String MAX_Int = "";
    public static String MAX_Native = "";

    public static int click = 2;
    public static int backclick = 2;

    public static int AdsClickCount = 0;
    public static int backAdsClickCount = 0;

    public static String Type1 = "admob";
    public static String Type2 = "fb";
    public static String Type3 = "";
    public static String Type4 = "";

    public static String MoreApps = "More+Apps";
    public static String PrivacyPolicy = "https://www.freeprivacypolicy.com/blog/privacy-policy-url/";

    public static int checkInAppUpdate = 0;

    public static Context context1;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        initializeWorkManager();


        sharedPreferencesInApp = getSharedPreferences("my", MODE_PRIVATE);
        editorInApp = sharedPreferencesInApp.edit();

        context1 = getApplicationContext();

        // OneSignal kodlarını tamamen kaldırdık - artık WorkManager'a gerek yok

        // AdMob initialization
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(
                            @NonNull InitializationStatus initializationStatus) {
                        Log.d("MyApplication", "AdMob initialized successfully");
                    }
                });

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();
    }

    // OneSignal artık kullanılmıyor, bu method'u da kaldırdık
    private void initializeWorkManager() {
        try {
            Configuration config = new Configuration.Builder()
                    .setMinimumLoggingLevel(Log.INFO)
                    .build();
            WorkManager.initialize(this, config);
            Log.d("MyApplication", "WorkManager initialized successfully");
        } catch (Exception e) {
            Log.e("MyApplication", "WorkManager initialization failed", e);
        }
    }
    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    private class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;
        private long loadTime = 0;

        public AppOpenAdManager() {
        }

        private void loadAd(Context context) {
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            if (MyApplication.getuser_balance() == 0 && !MyApplication.App_Open.equals("")) {

                isLoadingAd = true;
                AdRequest request = new AdRequest.Builder().build();
                AppOpenAd.load(
                        context,
                        App_Open,
                        request,
                        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                        new AppOpenAdLoadCallback() {
                            @Override
                            public void onAdLoaded(AppOpenAd ad) {
                                appOpenAd = ad;
                                isLoadingAd = false;
                                loadTime = (new Date()).getTime();
                                Log.d(LOG_TAG, "onAdLoaded.");
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                isLoadingAd = false;
                                Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                            }
                        });
            }
        }

        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        private boolean isAdAvailable() {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        private void showAdIfAvailable(@NonNull final Activity activity) {
            showAdIfAvailable(
                    activity,
                    new OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                        }
                    });
        }

        private void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            appOpenAd = null;
                            isShowingAd = false;
                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");
                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;
                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                        }
                    });

            isShowingAd = true;
            appOpenAd.show(activity);
        }
    }

    // SharedPreferences methods
    public static void setuser_balance(Integer user_balance) {
        editorInApp.putInt("user_balance", user_balance).commit();
    }

    public static Integer getuser_balance() {
        return sharedPreferencesInApp.getInt("user_balance", 0);
    }

    public static void setuser_onetime(Integer user_onetime) {
        editorInApp.putInt("user_onetime", user_onetime).commit();
    }

    public static Integer getuser_onetime() {
        return sharedPreferencesInApp.getInt("user_onetime", 0);
    }

    public static void setuser_permission(Integer user_permission) {
        editorInApp.putInt("user_permission", user_permission).commit();
    }

    public static Integer getuser_permission() {
        return sharedPreferencesInApp.getInt("user_permission", 0);
    }

    public static void setuser_guideline(Integer user_guideline) {
        editorInApp.putInt("user_guideline", user_guideline).commit();
    }

    public static Integer getuser_guideline() {
        return sharedPreferencesInApp.getInt("user_guideline", 0);
    }
}
*/

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.hdytyldrm.batterylevel.utils.PreferenceManager;

import java.util.Date;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private static PreferenceManager preferenceManager;
    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        // Senin PreferenceManager'ını kullan
        preferenceManager =new PreferenceManager(this,PreferenceManager.Settings.FILE);

        MobileAds.initialize(this, initializationStatus -> Log.d("MyApplication", "AdMob SDK başlatıldı."));

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = AppOpenAdManager.getInstance(this);
        appOpenAdManager.loadAd();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AppOpenAdManager getAppOpenAdManager() {
        return appOpenAdManager;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        if (currentActivity != null && appOpenAdManager != null) {
            if (appOpenAdManager.shouldShowAdBasedOnFrequency()) {
                appOpenAdManager.showAdIfAvailable(currentActivity, () -> {
                    Log.d("MyApplication", "App Open Ad completed");
                });
            }
        }
    }

    // PreferenceManager metodları
    public static void setuser_onetime(Integer user_onetime) {
        if (preferenceManager != null) {
            preferenceManager.editInt("user_onetime", user_onetime);
        }
    }

    public static Integer getuser_onetime() {
        if (preferenceManager != null) {
            return preferenceManager.getInt("user_onetime", 0);
        }
        return 0;
    }

    // ActivityLifecycleCallbacks metotları
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (appOpenAdManager != null && !appOpenAdManager.isShowingAd()) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}
    @Override
    public void onActivityPaused(@NonNull Activity activity) {}
    @Override
    public void onActivityStopped(@NonNull Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
}
