package com.demo.bluetoothbatterylevel.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;



public class UnifiedInterstitialAdManager {
    private static final String TAG = "InterstitialAdManager";
    private static final int READY_AD_PROGRESS_TIME = 500; // Yükleme ekranı bekleme süresi (ms)
    private static final long MIN_AD_INTERVAL = 10 * 1000; // Reklamlar arası minimum süre (10 saniye)

    private Context applicationContext;
    private InterstitialAd interstitialAd;
    private boolean isAdReady = false;
    private boolean isAdLoading = false;
    private boolean isAdShowing = false;
    private long lastAdShownTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    private CounterManager counterManager;

    private static UnifiedInterstitialAdManager instance;

    public static UnifiedInterstitialAdManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UnifiedInterstitialAdManager.class) {
                if (instance == null) {
                    instance = new UnifiedInterstitialAdManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public UnifiedInterstitialAdManager(Context context) {
        this.applicationContext = context;
        // NOT: Diğer yöneticiler (Subscription, Analytics) burada initialize edilebilir.
        // this.subscriptionManager = new SubscriptionManager(context);
        this.counterManager = CounterManager.getInstance(context);
        loadInterstitialAd();
    }

    public void loadInterstitialAd() {
        if (isAdLoading || isAdReady) {
            return;
        }
        isAdLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(applicationContext, AdsUnit.INTERSTITIAL, // DİKKAT: AdsUnit sınıfımızdan ID'yi alıyoruz
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        isAdReady = true;
                        isAdLoading = false;
                        Log.d(TAG, "Interstitial reklam başarıyla yüklendi");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        interstitialAd = null;
                        isAdReady = false;
                        isAdLoading = false;
                        Log.e(TAG, "Reklam yükleme hatası: " + error.getMessage());
                    }
                });
    }

    private boolean shouldShowAds() {
        // TODO: SubscriptionManager kontrolü buraya eklenebilir.
        // Örnek: if (subscriptionManager.isUserSubscribed()) return false;
        return true;
    }

    public void showInterstitialAdWithProgress(Activity activity, String screenName, Runnable onAdClosed) {
        if (activity == null || activity.isFinishing()) {
            if (onAdClosed != null) onAdClosed.run();
            return;
        }

        if (!shouldShowAds() || isAdShowing || (System.currentTimeMillis() - lastAdShownTime < MIN_AD_INTERVAL)) {
            if (onAdClosed != null) onAdClosed.run();
            return;
        }

        if (!isAdReady || interstitialAd == null) {
            Log.d(TAG, "Reklam hazır değil, yeniden yükleniyor.");
            if (!isAdLoading) {
                loadInterstitialAd();
            }
            if (onAdClosed != null) onAdClosed.run();
            return;
        }

        isAdShowing = true;
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        handler.postDelayed(() -> {
            if (activity.isFinishing()) {
                loadingDialog.dismissDialog();
                isAdShowing = false;
                if (onAdClosed != null) onAdClosed.run();
                return;
            }

            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    lastAdShownTime = System.currentTimeMillis();
                    resetAd();
                    loadingDialog.dismissDialog();
                    if (onAdClosed != null) onAdClosed.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                    resetAd();
                    loadingDialog.dismissDialog();
                    if (onAdClosed != null) onAdClosed.run();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Reklam gösterildiğinde sayaçları sıfırlayabiliriz
                    counterManager.resetCounter(CounterManager.CounterType.ACTIVITY);
                }
            });
            interstitialAd.show(activity);

        }, READY_AD_PROGRESS_TIME);
    }

    private void resetAd(){
        interstitialAd = null;
        isAdReady = false;
        isAdShowing = false;
        loadInterstitialAd();
    }

    public void incrementCounter(CounterManager.CounterType counterType) {
        counterManager.incrementCounter(counterType);
    }

    public boolean shouldShowEveryNCount(CounterManager.CounterType counterType, int threshold) {
        return counterManager.shouldShowEveryNCount(counterType, threshold);
    }
}