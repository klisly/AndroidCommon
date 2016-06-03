package com.klisly.common;

import android.util.Log;

/**
 * Log工具类，设置开关，防止发布版本时log信息泄露
 *
 * @author wizardholy{wizardholy@163.com}
 * @date 2014年11月8日上午12:02:51
 */

public class LogUtils {
    private static String TAG = "LogUtils";
    private static long start = 0;
    private static boolean isDebug = true;

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);

        }

    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }

    }

    public static void i(String tag, String msg) {

        if (isDebug) {
            Log.i(tag, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }

    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String TAG, Throwable ex) {
        if(!isDebug){
            return;
        }
        String logMessage = ex.getMessage();
        String logBody = Log.getStackTraceString(ex);
        Log.e(TAG, logMessage+"\n"+logBody);
    }

    public static void outCurrentTime(String tip) {
        if(!isDebug){
            return;
        }
        LogUtils.i(TAG, tip);
        outCurrentTime();
    }

    public static void outCurrentTime() {
        if(!isDebug){
            return;
        }
        if (start == 0) {
            start = System.currentTimeMillis();
            LogUtils.i(TAG, "curTime:" + start);
        } else {
            LogUtils.i(TAG, "curTime Span:" + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
        }
    }
}
