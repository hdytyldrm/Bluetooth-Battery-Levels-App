package com.demo.bluetoothbatterylevel.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceManager {

    private final SharedPreferences pref;
     Context context;

    public PreferenceManager(Context context, String fileName) {
        this.context=context;
        this.pref =context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }



    /**
     * 初回起動時に設定可能項目のデフォルト値を設定
     */



    public void editStr(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 設定可能項目の int を編集できる
     *
     * @param key String - 設定項目名 Settings クラスからどうぞ
     * @param value int - 編集後の値
     */
    public void editInt(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public void editBool(String key, boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void editLong(String key, long value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();
    }





    public String getStr(String key,String defaultValue) {
        return pref.getString(key, defaultValue);
    }

    /**
     * 設定可能項目の int を取得できる
     *
     * @param key String - 設定項目名 Settings クラスからどうぞ
     */
    public int getInt(String key,int  defaultValue) {
        return pref.getInt(key, defaultValue);
    }
    public long getLong(String key,long  defaultValue) {
        return pref.getLong(key, defaultValue);
    }
    /**
     * 設定可能項目の boolean を取得できる
     *
     * @param key String - 設定項目名 Settings クラスからどうぞ
     */
    public boolean getBool(String key,boolean defaultValue) {
        return pref.getBoolean(key, defaultValue);
    }

    /**
     * 設定可能項目が存在しているかチェックする
     *
     * @param key String - 設定項目名 Settings クラスからどうぞ
     * @return boolean - exists == true
     */
    public boolean exists(String key) {
        return pref.contains(key);
    }

    /**
     * 設定可能項目一覧
     */
    public static class Settings {
        /**
         * ファイル名
         */
        public final static String FILE = "bcon_settings";
        /**
         * 初期化されているか確認用 - boolean
         */
      //  public final static String INITIALIZE = "initialize";
        /**
         * バッテリー残量か寿命か判別用 - boolean
         */
     //  public final static String ENABLE_LEVEL = "enable_level";
        /**
         * アラーム有効 or 無効 - boolean
         */
       // public final static String ENABLE_ALARM = "enable_alarm";
        /**
         * 警告(通知)有効 or 無効 - boolean
         */

      //  public final static String ENABLE_WARN = "enable_warn";
        /**
         * アラームが発火する最大 % - int
         */
      // public final static String ALARM_MAX_PERCENT = "alarm_max_percent";
        /**
         * アラームが発火する最小 % - int
         */
     //   public final static String ALARM_MIN_PERCENT = "alarm_min_percent";
        /**
         * 警告が発火する最大 % - int
         */
      //  public final static String WARN_MAX_PERCENT = "warn_max_percent";
        /**
         * 警告が発火する最小 % - int
         */
        public final static String WARN_MIN_PERCENT = "warn_min_percent";



    }
}
