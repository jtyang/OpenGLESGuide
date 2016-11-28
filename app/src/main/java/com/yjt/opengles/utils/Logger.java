package com.yjt.opengles.utils;

import android.util.Log;

/**
 * 文件描述
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class Logger {

    private static final String TAG = "OpenGLES";

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }
}
