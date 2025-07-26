package com.demo.bluetoothbatterylevel.audio;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;

public class BluetoothAudioVolumeObserver {
    private final AudioManager mAudioManager;
    private BluetoothAudioVolumeContentObserver mBtAudioVolumeContentObserver;
    private final Context mContext;

    public BluetoothAudioVolumeObserver(Context context) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public void register(int i, BluetoothOnAudioVolumeChangedListener bluetoothOnAudioVolumeChangedListener) {
        this.mBtAudioVolumeContentObserver = new BluetoothAudioVolumeContentObserver(new Handler(), this.mAudioManager, i, bluetoothOnAudioVolumeChangedListener);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, this.mBtAudioVolumeContentObserver);
    }

    public void unregister() {
        if (this.mBtAudioVolumeContentObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mBtAudioVolumeContentObserver);
            this.mBtAudioVolumeContentObserver = null;
        }
    }
}
