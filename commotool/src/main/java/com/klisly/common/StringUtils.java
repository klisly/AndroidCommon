/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.klisly.common.dateutil.DateStyle;
import com.klisly.common.dateutil.DateUtil;
import com.klisly.similarity.LevenshteinDistanceStrategy;

/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    private static final String TAG = "StringUtils";
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    /** The Constant HEX_DIGITS. */
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F' };
    // private final static SimpleDateFormat dateFormater = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private final static SimpleDateFormat dateFormater2 = new
    // SimpleDateFormat("yyyy-MM-dd");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    /**
     * 将字符串转为日期类型
     *
     * @param sdate
     *
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            LogUtils.e(TAG, e);
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     *
     * @return
     */
    public static String toFriendlyTime(String sdate) {
        Date time = null;
        if (TimeUtil.isInEasternEightZones()) {
            time = toDate(sdate);
        } else {
            time = TimeUtil.transformTime(toDate(sdate),
                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
        }
        return toFriendlyTime(time.getTime());
    }

    public static String toFriendlyTime(long time) {
        return toFriendlyTime(new Date(time));
    }

    private static String toFriendlyTime(Date time) {
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        int curYear = DateUtil.getYear(cal.getTime());
        int paraYear = DateUtil.getYear(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        } else {
            if (curYear == paraYear) {
                ftime = DateUtil.DateToString(time, DateStyle.MM_DD);
            } else {
                ftime = DateUtil.DateToString(time, DateStyle.YYYY_MM_DD_CN);
            }
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     *
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     *
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input)) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     *
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            return false;
        }
        return emailer.matcher(email).matches();
    }

    public static double computeSimilarity(String stra, String strb) {
        LevenshteinDistanceStrategy strategy = new LevenshteinDistanceStrategy();
        return strategy.score(stra, strb);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     *
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     *
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     *
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     *
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return false;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     *
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                LogUtils.e(TAG, e);
            }
        }
        return res.toString();
    }

    /**
     * 手机号验证
     *
     * @param str
     *
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        //只做开头1后面是10位数字
        p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     *
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    public static String getCacheImageKey(String imageUri) {
        String key = imageUri;
        if (imageUri != null && imageUri.contains("blur")) {
            key = imageUri.substring(0, imageUri.lastIndexOf("blur"));
        } else if (imageUri != null && imageUri.contains("?")) {
            key = imageUri.substring(0, imageUri.lastIndexOf("?"));
        }
        return key;
    }

    public static String generate(String imageUri) {
        String key = imageUri;
        if (StringUtils.isEmpty(key)) {
            key = "default";
        }
        if (imageUri != null && imageUri.contains("blur")) {
            key = imageUri.substring(0, imageUri.lastIndexOf("blur"));
        } else if (imageUri != null && imageUri.contains("?")) {
            key = imageUri.substring(0, imageUri.lastIndexOf("?"));
        }
        byte[] md5 = getMD5(key.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;
    }

    public static String getTags(List<String> tags) {
        StringBuffer tagBuffer = new StringBuffer();
        tagBuffer.append("");
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                tagBuffer.append(tag).append("  ");
            }
        }
        return tagBuffer.toString();
    }

    public static boolean isNotEmpty(String title) {
        return !isEmpty(title);
    }

    /**
     * Gets the random bcs name.
     *
     * @return the random bcs name
     */
    public static String getRandomBcsName() {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < 32; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        buffer = new StringBuffer(getMD5(System.currentTimeMillis() + sb.toString()).toLowerCase());
        buffer.insert(8, "-");
        buffer.insert(13, "-");
        buffer.insert(18, "-");
        buffer.insert(23, "-");
        return buffer.toString();
    }

    /**
     * Gets the m d5.
     *
     * @param message the message
     * @return the m d5
     */
    public static String getMD5(String message) {
        String digest = message;
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(message.getBytes("UTF-8"));
            digest = toHexString2(algorithm.digest());
            digest = digest.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digest;
    }

    /**
     * To hex string2.
     *
     * @param b the b
     * @return the string
     */
    public static String toHexString2(byte[] b) {
        /*
         * String str = new String(b); System.out.println(str); try { //b = str.getBytes("UTF-8"); } catch
         * (UnsupportedEncodingException e) { // TODO Auto-generated catch block e.printStackTrace(); }
         */
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
}
