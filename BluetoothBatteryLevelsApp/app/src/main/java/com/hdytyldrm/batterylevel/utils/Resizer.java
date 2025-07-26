package com.hdytyldrm.batterylevel.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class Resizer {
    public static final int NEXT_CLICK_TIME = 1000;
    public static int SCALE_HEIGHT = 1920;
    public static int SCALE_WIDTH = 1080;
    public static int height;
    public static String key;
    public static int width;

    public static void getheightandwidth(Context context) {
        getHeight(context);
        getwidth(context);
    }

    public static int getwidth(Context context) {
        int i = context.getResources().getDisplayMetrics().widthPixels;
        width = i;
        return i;
    }

    public static int getHeight(Context context) {
        int i = context.getResources().getDisplayMetrics().heightPixels;
        height = i;
        return i;
    }

    public static void setHeight(Context context, View view, int i) {
        view.getLayoutParams().height = (context.getResources().getDisplayMetrics().heightPixels * i) / SCALE_HEIGHT;
    }

    public static void setWidth(Context context, View view, int i) {
        view.getLayoutParams().width = (context.getResources().getDisplayMetrics().widthPixels * i) / SCALE_WIDTH;
    }

    public static int setHeight(int i) {
        return (height * i) / 1920;
    }

    public static int setWidth(int i) {
        return (width * i) / 1080;
    }

    public static void setSize(View view, int i, int i2) {
        view.getLayoutParams().height = setHeight(i2);
        view.getLayoutParams().width = setWidth(i);
    }

    public static void setHeightByWidth(Context context, View view, int i) {
        view.getLayoutParams().height = (context.getResources().getDisplayMetrics().widthPixels * i) / SCALE_WIDTH;
    }

    public static void setSize(View view, int i, int i2, boolean z) {
        if (z) {
            view.getLayoutParams().height = setWidth(i2);
            view.getLayoutParams().width = setWidth(i);
            return;
        }
        view.getLayoutParams().height = setHeight(i2);
        view.getLayoutParams().width = setHeight(i);
    }

    public static void setMargins(View view, int i, int i2, int i3, int i4) {
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(setWidth(i), setHeight(i2), setWidth(i3), setHeight(i4));
    }

    public static void setPadding(View view, int i, int i2, int i3, int i4) {
        view.setPadding(i, i2, i3, i4);
    }

    public static void setHeightWidth(Context context, View view, int i, int i2) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int i3 = (displayMetrics.widthPixels * i) / SCALE_WIDTH;
        int i4 = (displayMetrics.heightPixels * i2) / SCALE_HEIGHT;
        view.getLayoutParams().width = i3;
        view.getLayoutParams().height = i4;
    }

    public static void setHeightWidthAsWidth(Context context, View view, int i, int i2) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int i3 = (displayMetrics.widthPixels * i) / SCALE_WIDTH;
        int i4 = (displayMetrics.widthPixels * i2) / SCALE_WIDTH;
        view.getLayoutParams().width = i3;
        view.getLayoutParams().height = i4;
    }

    public static void setMargins(Context context, View view, int i, int i2, int i3, int i4) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int i5 = (displayMetrics.widthPixels * i) / SCALE_WIDTH;
        int i6 = (displayMetrics.heightPixels * i2) / SCALE_HEIGHT;
        int i7 = (displayMetrics.widthPixels * i3) / SCALE_WIDTH;
        int i8 = (displayMetrics.heightPixels * i4) / SCALE_HEIGHT;
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(i5, i6, i7, i8);
            view.requestLayout();
        }
    }

    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static void setMarginLeft(Context context, View view, int i) {
        int i2 = (context.getResources().getDisplayMetrics().widthPixels * i) / SCALE_WIDTH;
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(i2, 0, 0, 0);
            view.requestLayout();
        }
    }

    public static void setPadding(Context context, View view, int i, int i2, int i3, int i4) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        view.setPadding((displayMetrics.widthPixels * i) / SCALE_WIDTH, (displayMetrics.heightPixels * i2) / SCALE_HEIGHT, (displayMetrics.widthPixels * i3) / SCALE_WIDTH, (displayMetrics.heightPixels * i4) / SCALE_HEIGHT);
    }

    public static void setHeightWidth2(Context context, View view, int i, int i2) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int i3 = (displayMetrics.widthPixels * i) / SCALE_WIDTH;
        int i4 = (displayMetrics.widthPixels * i2) / SCALE_WIDTH;
        view.getLayoutParams().width = i3;
        view.getLayoutParams().height = i4;
    }
}
