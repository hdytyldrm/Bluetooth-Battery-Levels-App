package com.demo.bluetoothbatterylevel.ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class AdsGeneral {

    private static final String TAG = "AdsGeneral";
    private final Activity activity;
    // Constructor
    public AdsGeneral(Activity activity) {
        this.activity = activity;
    }

    /**
     * Verilen AdView'a standart bir banner reklam yükler.
     * @param adView Reklamın yükleneceği XML'deki AdView bileşeni.
     */

    public void loadBannerAd(final AdView adView, String adUnitId) {
        if (adView == null) {
            Log.e(TAG, "AdView is null. Cannot load ad.");
            return;
        }

        // XML'de ID olmasa bile, ID'yi ve boyutu burada programatik olarak ayarlıyoruz.
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AdSize.BANNER);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
                Log.d(TAG, "Banner Ad Loaded Successfully for unit: " + adUnitId);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.setVisibility(View.GONE);
                Log.e(TAG, "Banner Ad Failed for unit " + adUnitId + ": " + loadAdError.getMessage());
            }
        });
    }
    public void loadAdaptiveBanner(final FrameLayout adContainer) {
        if (adContainer == null) {
            Log.e(TAG, "FrameLayout container is null. Cannot load ad.");
            return;
        }

        // AdView'ı programatik olarak oluştur
        AdView adView = new AdView(activity);
        adView.setAdUnitId(AdsUnit.BANNER); // AdsUnit sınıfımızdan ID'yi al

        // AdContainer'ı temizle ve yeni AdView'ı ekle
        adContainer.removeAllViews();
        adContainer.addView(adView);

        // Ekran genişliğine göre Adaptive AdSize hesapla
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);

        adView.setAdSize(adSize);

        // Reklamı yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainer.setVisibility(View.VISIBLE);
                Log.d(TAG, "Adaptive Banner Ad Loaded Successfully.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adContainer.setVisibility(View.GONE);
                Log.e(TAG, "Adaptive Banner Ad Failed to Load: " + loadAdError.getMessage());
            }
        });
    }
}
