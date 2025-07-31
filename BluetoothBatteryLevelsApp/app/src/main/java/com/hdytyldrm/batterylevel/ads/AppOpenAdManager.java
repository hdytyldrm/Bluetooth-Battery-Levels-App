package com.hdytyldrm.batterylevel.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.hdytyldrm.batterylevel.utils.PreferenceManager;

public class AppOpenAdManager {
    private static final String TAG = "AppOpenAdManager";
    private static volatile AppOpenAdManager instance;
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;
    private final Context context;
    private final PreferenceManager preferenceManager;

    // Test App Open Ad ID - Gerçek projenizde bunu değiştirin

    // PreferenceManager keys
    private static final String KEY_LOAD_TIME = "app_open_ad_load_time";
    private static final String KEY_SHOW_TIME = "app_open_ad_show_time";
    private static final String KEY_CLICK_COUNT = "app_open_ad_clicks";

    private AppOpenAdManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferenceManager = new PreferenceManager(context,PreferenceManager.Settings.FILE);
    }

    public static AppOpenAdManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AppOpenAdManager.class) {
                if (instance == null) {
                    instance = new AppOpenAdManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * Reklamı yükle
     */
    public void loadAd() {
        if (isLoadingAd || isAdAvailable()) {
            Log.d(TAG, "Ad already loading or available, skipping load");
            return;
        }

        isLoadingAd = true;
        AdRequest adRequest = new AdRequest.Builder().build();

        Log.d(TAG, "Loading App Open Ad...");

        AppOpenAd.load(
                context,
                AdsUnit.APP_OPEN,
                adRequest,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = System.currentTimeMillis();

                        // PreferenceManager'a kaydet
                        preferenceManager.editLong(KEY_LOAD_TIME, loadTime);

                        Log.d(TAG, "App Open Ad loaded successfully");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                        Log.e(TAG, "Failed to load app open ad: " +
                                loadAdError.getMessage() + " (Code: " + loadAdError.getCode() + ")");
                    }
                }
        );
    }

    /**
     * Reklamı mevcut aktivitede göster
     */
    public void showAdIfAvailable(@NonNull final Activity activity,
                                  @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {

        if (activity.isFinishing()) {
            Log.d(TAG, "Activity is finishing, not showing ad");
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }

        if (isShowingAd) {
            Log.d(TAG, "Ad is already showing");
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "Ad is not ready yet");
            onShowAdCompleteListener.onShowAdComplete();
            // Yeni reklam yükle
            loadAd();
            return;
        }

        Log.d(TAG, "Showing App Open Ad");

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                isShowingAd = false;

                // PreferenceManager'a gösterim zamanını kaydet
                preferenceManager.editLong(KEY_SHOW_TIME, System.currentTimeMillis());

                Log.d(TAG, "App Open Ad dismissed");
                onShowAdCompleteListener.onShowAdComplete();

                // Yeni reklam yükle
                loadAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                appOpenAd = null;
                isShowingAd = false;

                Log.e(TAG, "Failed to show App Open Ad: " + adError.getMessage());
                onShowAdCompleteListener.onShowAdComplete();

                // Yeni reklam yükle
                loadAd();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                isShowingAd = true;
                Log.d(TAG, "App Open Ad showed full screen");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "App Open Ad clicked");

                // Tıklama sayısını artır
                int clickCount = preferenceManager.getInt(KEY_CLICK_COUNT, 0);
                preferenceManager.editInt(KEY_CLICK_COUNT, clickCount + 1);

                super.onAdClicked();
            }
        });

        isShowingAd = true;
        appOpenAd.show(activity);
    }

    /**
     * Reklamın şu anda gösterilip gösterilmediğini kontrol et
     */
    public boolean isShowingAd() {
        return isShowingAd;
    }

    /**
     * Reklamın mevcut ve geçerli olup olmadığını kontrol et
     */
    private boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    /**
     * Reklamın yüklenme zamanını kontrol et (4 saat geçerliliği)
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = System.currentTimeMillis() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Callback interface
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    /**
     * PreferenceManager ile debug bilgileri
     */
    public void printDebugInfo() {
        Log.d(TAG, "=== App Open Ad Debug Info ===");
        Log.d(TAG, "Ad Available: " + isAdAvailable());
        Log.d(TAG, "Is Loading: " + isLoadingAd);
        Log.d(TAG, "Is Showing: " + isShowingAd);
        Log.d(TAG, "Load Time: " + loadTime);
        Log.d(TAG, "Ad Object: " + (appOpenAd != null ? "Not null" : "null"));

        // PreferenceManager'dan bilgileri al
        Log.d(TAG, "Last Load Time (Pref): " + preferenceManager.getLong(KEY_LOAD_TIME, 0L));
        Log.d(TAG, "Last Show Time (Pref): " + preferenceManager.getLong(KEY_SHOW_TIME, 0L));
        Log.d(TAG, "Total Clicks (Pref): " + preferenceManager.getInt(KEY_CLICK_COUNT, 0));
        Log.d(TAG, "=============================");
    }

    /**
     * Reklam gösterim sıklığını kontrol et
     */
    public boolean shouldShowAdBasedOnFrequency() {
        long lastShowTime = preferenceManager.getLong(KEY_SHOW_TIME, 0L);
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastShowTime;

        // 30 saniye içinde tekrar gösterme (spam önleme)
        long minInterval = 30 * 1000; // 30 saniye

        boolean shouldShow = timeDifference > minInterval;
        Log.d(TAG, "Should show ad based on frequency: " + shouldShow +
                " (Last shown: " + (timeDifference / 1000) + " seconds ago)");

        return shouldShow;
    }

}
