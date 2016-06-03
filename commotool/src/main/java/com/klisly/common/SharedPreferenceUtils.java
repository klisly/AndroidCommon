/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

/**
 * 首选项工具类
 * 
 * @author adison
 * 
 */
public class SharedPreferenceUtils {
	private static final String TAG = "SharedPreferenceUtils";
	private Context context;
	private SharedPreferences sp = null;
	private Editor editor = null;

	/**
	 * 创建默认sp
	 * 
	 * @param context
	 */
	public SharedPreferenceUtils(Context context) {
		this(context, PreferenceManager.getDefaultSharedPreferences(context));
	}

	/**
	 * 通过文件名创建sp
	 * 
	 * @param context
	 * @param filename
	 */
	public SharedPreferenceUtils(Context context, String filename) {
		this(context, context.getSharedPreferences(filename,
				Context.MODE_WORLD_WRITEABLE));
	}

	/**
	 * 通过sp创建sp
	 * 
	 * @param context
	 * @param sp
	 */
	public SharedPreferenceUtils(Context context, SharedPreferences sp) {
		this.context = context;
		this.sp = sp;
		editor = sp.edit();
	}

	/**
	 * 记录日期，决定是否数据是否需要改动
	 *
	 * @return
	 */
	public static String getDateByNumber() {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd",
				new Locale("zh"));
		String cur = s.format(new Date());
		return cur;
	}

	// Set

	public SharedPreferences getInstance() {
		return sp;
	}

	// Boolean
	public void setValue(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void setValue(int resKey, boolean value) {
		setValue(this.context.getString(resKey), value);
	}

	// Float
	public void setValue(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public void setValue(int resKey, float value) {
		setValue(this.context.getString(resKey), value);
	}

	// Integer
	public void setValue(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void setValue(int resKey, int value) {
		setValue(this.context.getString(resKey), value);
	}

	// Long
	public void setValue(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	public void setValue(int resKey, long value) {
		setValue(this.context.getString(resKey), value);
	}

	// String
	public void setValue(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	// Get

	public void setValue(int resKey, String value) {
		setValue(this.context.getString(resKey), value);
	}

	// Boolean
	public boolean getValue(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

	public boolean getValue(int resKey, boolean defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Float
	public float getValue(String key, float defaultValue) {
		return sp.getFloat(key, defaultValue);
	}

	public float getValue(int resKey, float defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Integer
	public int getValue(String key, int defaultValue) {
		return sp.getInt(key, defaultValue);
	}

	public int getValue(int resKey, int defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Long
	public long getValue(String key, long defaultValue) {
		return sp.getLong(key, defaultValue);
	}

	public long getValue(int resKey, long defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// String
	public String getValue(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	public String getValue(int resKey, String defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Delete
	public void remove(String key) {
		editor.remove(key);
		editor.commit();
	}

	public void clear() {
		editor.clear();
		editor.commit();
	}

	/**
	 * 是否第一次启动应用
	 *
	 * @param context
	 * @return
	 */
	public boolean isFirstStart(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			int curVersion = info.versionCode;
			int lastVersion = sp.getInt("version", 0);
			// 如果当前版本大于上次版本，该版本属于第一次启动
			// 将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
			return curVersion > lastVersion;
		} catch (NameNotFoundException e) {
			LogUtils.e(TAG, e);
		}

		return false;
	}

	/**
	 * 是否第一次安装应用
	 *
	 * @param context
	 * @return
	 */
	public boolean isFirstInstall(Context context) {
		int install = sp.getInt("first_install", 0);
		return install == 0;

	}

	/**
	 * 应用已启动
	 *
	 * @param context
	 */
	public void setStarted(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			int curVersion = info.versionCode;
			sp.edit().putInt("version", curVersion).commit();
		} catch (NameNotFoundException e) {
			LogUtils.e(TAG, e);
		}
	}

	/**
	 * 应用已安装并启动
	 *
	 * @param context
	 */
	public void setInstalled(Context context) {
		sp.edit().putInt("first_install", 1).commit();
	}

	/**
	 * 是否需要改变数据
	 *
	 * @param context
	 * @param openID
	 * @return
	 */
	public  boolean needChangeIndexContent(Context context, String openID) {

		String save = sp.getString(openID, "");
		String cur = getDateByNumber();
		return !save.equals(cur);
	}
	
	/**
	 * 保存更新日期
	 *
	 * @param context
	 * @param openID
	 */
	public void saveChangeIndexContent(Context context, String openID) {

		String cur = getDateByNumber();
		sp.edit().putString(openID, cur).commit();
	}
}
