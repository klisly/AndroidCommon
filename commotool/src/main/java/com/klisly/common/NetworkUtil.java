/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * @author  网络工具类
 */
public class NetworkUtil{

	/**
	 * 返回网络是否可用。需要权限：
	 * <p>
	 * <b> < uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE" /> </b>
	 * </p>
	 * 
	 * @param context
	 *            上下文
	 * @return 网络可用则返回true，否则返回false
	 */
	public static boolean isAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isAvailable();
	}

	 /**
     * 判断网络连接是否可用
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        // 获取手机所有连接管理对象（包括wifi，net等连接的管理）
        ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            // 获取网络连接管理的对象
            NetworkInfo info=connectivity.getActiveNetworkInfo();
            if(info != null && info.isConnected()) {
                // 判断当前网络是否已经连接
                if(info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;

    }
	
//	/**
//	 * 判断网络连接状态
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public static String getNetType(Context context) {
//
//		try {
//			ConnectivityManager connectivity = (ConnectivityManager) context
//					.getSystemService(Context.CONNECTIVITY_SERVICE);
//			if (connectivity != null) {
//
//				NetworkInfo info = connectivity.getActiveNetworkInfo();
//				if (info != null && info.isConnected()) {
//
//					if (info.getState() == NetworkInfo.State.CONNECTED) {
//						if (info.getType() == ConnectivityManager.TYPE_WIFI) {
//							// wifi
//							return Constant.NETWORK_TYPE_WIFI;
//						} else {
//							// 手机网络
//							return Constant.NETWORK_TYPE_MOBILE;
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			// 网络错误
//			return Constant.NETWORK_TYPE_ERROR;
//		}
//		// 网络错误
//		return Constant.NETWORK_TYPE_ERROR;
//
//	}

	/**
	 * 返回Wifi是否启用
	 * 
	 * @param context
	 *            上下文
	 * @return Wifi网络可用则返回true，否则返回false
	 */
	public static boolean isWIFIActivate(Context context) {
		return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.isWifiEnabled();
	}

	/**
	 * 修改Wifi状态
	 * 
	 * @param context
	 *            上下文
	 * @param status
	 *            true为开启Wifi，false为关闭Wifi
	 */
	public static void changeWIFIStatus(Context context, boolean status) {
		((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.setWifiEnabled(status);
	}
}
