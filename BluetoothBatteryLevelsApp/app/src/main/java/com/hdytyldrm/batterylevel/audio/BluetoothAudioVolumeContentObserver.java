package com.hdytyldrm.batterylevel.audio;

import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;

public class BluetoothAudioVolumeContentObserver extends ContentObserver {
    private final AudioManager mAudioManager;
    private final int mAudioStreamType;
    private int mLastVolume;
    private final BluetoothOnAudioVolumeChangedListener mListener;

    public BluetoothAudioVolumeContentObserver(Handler handler, AudioManager audioManager, int i, BluetoothOnAudioVolumeChangedListener bluetoothOnAudioVolumeChangedListener) {
        super(handler);
        this.mAudioManager = audioManager;
        this.mAudioStreamType = i;
        this.mListener = bluetoothOnAudioVolumeChangedListener;
        this.mLastVolume = audioManager.getStreamVolume(i);
    }

    @Override
    public void onChange(boolean z, Uri uri) {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null && this.mListener != null) {
            int streamMaxVolume = audioManager.getStreamMaxVolume(this.mAudioStreamType);
            int streamVolume = this.mAudioManager.getStreamVolume(this.mAudioStreamType);
            if (streamVolume != this.mLastVolume) {
                this.mLastVolume = streamVolume;
                this.mListener.onAudioVolumeChanged(streamVolume, streamMaxVolume);
            }
        }
    }

    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }
}
