package com.hdytyldrm.batterylevel.notification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import com.hdytyldrm.batterylevel.R;

public class WebActivity extends AppCompatActivity {
    String link;
    SharedPreferences.Editor myEdit;
    public ProgressDialog pd;
    RelativeLayout rl_loadingscreen;
    SharedPreferences sharedPreferences;
    String type;
    public WebView webView;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_web);
        Log.e("getmetype", "web ");
        getWindow().addFlags(128);
        Log.e("getmetype", "next ");
        Intent intent = getIntent();
        if (intent != null) {
            this.link = intent.getStringExtra("link");
            this.type = getIntent().getStringExtra("type");
        }

        WebView webView2 = (WebView) findViewById(R.id.webView1);
        this.webView = webView2;
        webView2.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setUseWideViewPort(true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_loadingscreen);
        this.rl_loadingscreen = relativeLayout;
        relativeLayout.setVisibility(0);
        SharedPreferences sharedPreferences2 = getSharedPreferences("bdcPref", 0);
        this.sharedPreferences = sharedPreferences2;
        this.myEdit = sharedPreferences2.edit();
        if (!this.sharedPreferences.getBoolean("hasMatch", false) || !this.type.equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_3D)) {
            next();
        } else {
            next();
        }
    }

    
    public void next() {
        showads();
    }

    public void showads() {
        onPreExecute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WebActivity.this.pd.hide();
                WebActivity.this.adsss();
            }
        }, 3000);
    }

/*
    private void loadwebview(String str) {
        this.rl_loadingscreen.setVisibility(8);
        this.webView.setWebViewClient(new WebViewClient() {
            public ProgressDialog proDial1;

            @Override
            public void onLoadResource(WebView webView, String str) {
                if (this.proDial1 == null) {
                    ProgressDialog progressDialog = new ProgressDialog(WebActivity.this);
                    this.proDial1 = progressDialog;
                    progressDialog.setMessage("Loading...");
                    this.proDial1.setCancelable(true);
                    this.proDial1.show();
                }
            }

            @Override
            public void onPageFinished(WebView webView, String str) {
                this.proDial1.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return true;
            }
        });
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.loadUrl(str);
    }
*/

    private void loadwebview(String str) {
        this.rl_loadingscreen.setVisibility(8);
        this.webView.setWebViewClient(new WebViewClient() {
            public ProgressDialog proDial1;

            @Override
            public void onLoadResource(WebView webView, String str) {
                if (this.proDial1 == null) {
                    ProgressDialog progressDialog = new ProgressDialog(WebActivity.this);
                    this.proDial1 = progressDialog;
                    progressDialog.setMessage("Loading...");
                    this.proDial1.setCancelable(true);
                    this.proDial1.show();
                }
            }

            @Override
            public void onPageFinished(WebView webView, String str) {
                if (this.proDial1 != null) {
                    this.proDial1.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                // GÜVENLİK: Sadece HTTPS URL'lere izin ver
                if (str.startsWith("https://")) {
                    webView.loadUrl(str);
                    return true;
                } else {
                    Log.w("WebActivity", "Non-HTTPS URL blocked: " + str);
                    return false;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                // GÜVENLİK: SSL hatalarında sayfayı yükleme
                handler.cancel();
                Log.e("WebActivity", "SSL Error detected, cancelling load");
            }
        });

        // GÜVENLİK AYARLARI
        android.webkit.WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(false); // YENİ: File access kapat
        webSettings.setAllowContentAccess(false); // YENİ: Content access kapat
        webSettings.setAllowFileAccessFromFileURLs(false); // YENİ: File URL access kapat
        webSettings.setAllowUniversalAccessFromFileURLs(false); // YENİ: Universal access kapat
        webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW); // YENİ: Mixed content engelle

        // GÜVENLİK: Sadece HTTPS URL'leri yükle
        if (str != null && str.startsWith("https://")) {
            this.webView.loadUrl(str);
        } else {
            Log.e("WebActivity", "Invalid or non-HTTPS URL: " + str);
            finish(); // Activity'yi kapat
        }
    }
    public void onPreExecute() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.pd = progressDialog;
        progressDialog.setTitle("Please Wait");
        this.pd.setMessage("Please wait...");
        this.pd.setCancelable(false);
    }

    public void adsss() {
        if (this.sharedPreferences.getString("colorfulldayfulls_show_atnotific", "0").equalsIgnoreCase("1")) {
            loadwebview(this.link);
        } else {
            loadwebview(this.link);
        }
        Log.d("TAG", "The interstitial ad wasn't ready yet.");
    }

    @Override
    public void onBackPressed() {
        if (this.sharedPreferences.getString("colorfulldayfulls_show_onexitnotifi", "0").equalsIgnoreCase("1")) {
            finishAffinity();
        } else if (this.sharedPreferences.getString("colorfullday_show_exitinnotifi", "0").equalsIgnoreCase("1")) {
            finishAffinity();
        } else {
            finishAffinity();
        }
    }
}
