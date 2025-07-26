package com.hdytyldrm.batterylevel.ads;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;


import com.hdytyldrm.batterylevel.R;

import java.lang.ref.WeakReference;

public class LoadingDialog {
    private static final String TAG = "LoadingDialog";
    private Activity activity;
    public AlertDialog dialog;
    private WeakReference<Activity> activityRef;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        this.activityRef = new WeakReference<>(activity);
    }

    public void startLoadingDialog() {
        Log.i(TAG, "startLoadingDialog: ");
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            Log.w(TAG, "Aktivite geçersiz, dialog başlatılamaz.");
            return;
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.custom_dialog, null));
            builder.setCancelable(true);

            if (!activity.isFinishing()) {
                dialog = builder.create();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // Arka plandaki karartmayı önler
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Arka planı şeffaf yapar
                dialog.show();
                Log.i(TAG, "LoadingDialog başarıyla başlatıldı.");
            }
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG, "BadTokenException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Dialog başlatma hatası: " + e.getMessage());
        }
    }

    public void dismissDialog() {
        Log.i(TAG, "dismissDialog: ");
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            Log.w(TAG, "Aktivite geçersiz, dialog kapatılmadan temizleniyor.");
            dialog = null; // Referansı temizle
            activityRef.clear(); // WeakReference'ı temizle
            return;
        }

        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                Log.i(TAG, "LoadingDialog başarıyla kapatıldı.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error dismissing dialog: " + e.getMessage());
        } finally {
            dialog = null; // Her durumda dialog referansını temizle
            activityRef.clear(); // Her durumda WeakReference'ı temizle
        }
    }
}