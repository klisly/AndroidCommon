package com.klisly.common;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

/**
 * 语音播放类<br>
 * Copyright 2012 Baidu Online Network Technology (Beijing) Co., Ltd. All rights reserved.<br>
 * VoicePlayHelper.java v1.0.0 created on 2014-11-3 下午4:55:03 <br>
 *
 * @author wangtianfei01@baidu.com
 */
public class VoicePlayHelper {

    private static final String TAG = "VoiceHelper";
    /**
     * 状态 播放
     */
    public static final int STATUS_PLAY = 1;
    /**
     * 状态 暂停
     */
    public static final int STATUS_PAUSE = 2;
    /**
     * 状态 完成
     */
    public static final int STATUS_FINISH = 3;

    /**
     * VoicePlayHelper instance.
     */
    private static VoicePlayHelper mVoiceHeler;
    /**
     * LeftVolume instance.
     */
    private float mLeftVolume;
    /**
     * RightVolume instance.
     */
    private float mRightVolume;
    /**
     * Context instance.
     */
    private Context mContext;
    /**
     * MediaPlayer instance.
     */
    private MediaPlayer mSpeakerPlayer;
    /**
     * MediaPlayer instance.
     */
    private MediaPlayer mInCallPlayer;
    /**
     * 是否已暂停.
     */
    private boolean mIsPaused;
    /**
     * 当前正在播放的语音地址.
     */
    private String mCurrentMusic = "";
    /**
     * 是否是扩音器播放.
     */
    private volatile boolean isUsingSpeaker = true;
    /**
     * 切换播放模式前的播放位置.
     */
    private int currPlayPosition = 0;
    /**
     * audio manager
     **/
    private AudioManager audioManager;
    /**
     * 是否播放录音有焦点
     **/
    private boolean mAudioFocus = false;
    /**
     * 记录系统设置播放的模式
     **/
    private int audioSysMode;
    /**
     * 记录系统是否使用扬声器
     **/
    private boolean audioSysIsUsingSpeaker;

    /**
     * 调用语音播放的时间间隔
     **/
    private static long INTERVAL = 1000;
    /**
     * 上次调用时间
     **/
    private long lastInvokeTime = 0;

    private OnStatusChangeListener listener = null;

    /**
     * 获取Audio焦点监听器
     **/
    private OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        /**
         * listen foucus changes
         *
         * @param focusChange focus state
         */
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    LogUtils.i(TAG, "AudioFocusChange AUDIOFOCUS_GAIN");
                    mAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    LogUtils.i(TAG, "AudioFocusChange AUDIOFOCUS_GAIN_TRANSIENT");
                    mAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    LogUtils.i(TAG, "AudioFocusChange AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                    mAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    LogUtils.i(TAG, "AudioFocusChange AUDIOFOCUS_LOSS");
                    mAudioFocus = false;
                    stopVoice();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    LogUtils.i(TAG, "AudioFocusChange AUDIOFOCUS_LOSS_TRANSIENT");
                    mAudioFocus = false;
                    stopVoice();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    LogUtils.i(TAG, "AudioFocusChange AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    mAudioFocus = false;
                    stopVoice();
                    break;
                default:
                    LogUtils.i(TAG, "AudioFocusChange focus = " + focusChange);
                    break;
            }
        }
    };

    public OnStatusChangeListener getListener() {
        return listener;
    }

    public void setListener(OnStatusChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 播放状态改变接口
     */
    public interface OnStatusChangeListener {

        /**
         * 语音状态变更接口
         */
        public void onVoiceStatusChange(String path, int status);
    }

    private VoicePlayHelper(Context context) {
        this.mContext = context;
        initData();
    }

    public static VoicePlayHelper getVoicePlayHelper(Context context) {
        if (mVoiceHeler == null) {
            mVoiceHeler = new VoicePlayHelper(context);
        }
        mVoiceHeler.checkValidation();
        return mVoiceHeler;
    }

    private void checkValidation() {
        if (mSpeakerPlayer == null || mInCallPlayer == null) {
            initData();
        }
    }

    // 初始化一些数据
    private void initData() {

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioSysMode = audioManager.getMode();
        audioSysIsUsingSpeaker = audioManager.isSpeakerphoneOn();
        if (isUsingSpeaker) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);
        }

        // audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

        float audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

        mLeftVolume = volumnRatio;
        mRightVolume = volumnRatio;
        mSpeakerPlayer = null;
        mInCallPlayer = null;
        mIsPaused = false;

        mSpeakerPlayer = new MediaPlayer();
        mSpeakerPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mSpeakerPlayer.setVolume(mLeftVolume, mRightVolume);
        mSpeakerPlayer.setLooping(false);

        mInCallPlayer = new MediaPlayer();
        mInCallPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mInCallPlayer.setVolume(mLeftVolume, mRightVolume);
        mInCallPlayer.setLooping(false);
    }

    /**
     * 还原系统的播放设置
     */
    public void resetAudioSetting() {
        audioManager.setMode(audioSysMode);
        audioManager.setSpeakerphoneOn(audioSysIsUsingSpeaker);
    }

    /**
     * 释放资源
     */
    public void release() {
        abandonAudioFocus();
        resetAudioSetting();
        if (mSpeakerPlayer != null) {
            mSpeakerPlayer.release();
            mSpeakerPlayer = null;
        }
        if (mInCallPlayer != null) {
            mInCallPlayer.release();
            mInCallPlayer = null;
        }
    }

    /**
     * @param path
     */
    public synchronized void playVoice(String path, OnStatusChangeListener listener) {
        if (listener == null) { // Listener 不能为空
            return;
        }
        this.listener = listener;
        if (lastInvokeTime + INTERVAL > System.currentTimeMillis()) {
            return;
        }
        lastInvokeTime = System.currentTimeMillis();

        File file = new File(path);

        if (file == null || !file.exists()) {
            this.currPlayPosition = 0;
            this.mCurrentMusic = null;
            listener.onVoiceStatusChange(path, STATUS_FINISH);
            return;
        }

        if (mCurrentMusic == null) {
            listener.onVoiceStatusChange(path, STATUS_PLAY);
        } else {
            this.stopVoice();
            this.currPlayPosition = 0;
        }

        play(path);
    }

    /**
     * 播放
     *
     * @param path
     */
    private synchronized void play(final String path) {

        try {
            mCurrentMusic = path;
            if (isUsingSpeaker) {
                audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                requestAudioFocus();
                mSpeakerPlayer.reset();
                mSpeakerPlayer.setDataSource(path);
                mSpeakerPlayer.prepare();
                mSpeakerPlayer.setOnPreparedListener(new OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.seekTo(currPlayPosition);
                        mp.start();
                        if(listener != null){
                            listener.onVoiceStatusChange(path, STATUS_PLAY);
                        }
                    }
                });
                mSpeakerPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        abandonAudioFocus();
                        resetAudioSetting();
                        listener.onVoiceStatusChange(path, STATUS_FINISH);
                    }
                });
                // LogUtils.i(TAG, "onPlayStatusChnage onSensorPlayModeChanged isUsingSpeaker" + currMessage);
            } else {

                audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(false);
                requestAudioFocus();
                mInCallPlayer.reset();
                mInCallPlayer.setDataSource(path);
                mInCallPlayer.prepare();
                mInCallPlayer.start();
                mInCallPlayer.setOnPreparedListener(new OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                    }
                });
                mInCallPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        abandonAudioFocus();
                        resetAudioSetting();
                    }
                });
                // ni LogUtils.i(TAG, "onPlayStatusChnage onSensorPlayModeChanged isUsingSpeaker false" + currMessage);
            }

            this.mIsPaused = false;
        } catch (Exception e) {
            LogUtils.e(TAG, "playBackgroundMusic: error state" + e);
        }
    }

    public void prepare() {

        try {
            if (mSpeakerPlayer != null) {
                mSpeakerPlayer.reset();
                mSpeakerPlayer.setDataSource("");
                mSpeakerPlayer.prepareAsync();
            }

            if (mInCallPlayer != null) {
                mInCallPlayer.reset();
                mInCallPlayer.setDataSource("");
                mInCallPlayer.prepareAsync();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录语音播放位置
     */
    private void markPlayPosition() {
        if (this.isUsingSpeaker) {
            if (mSpeakerPlayer != null) {
                currPlayPosition = mSpeakerPlayer.getCurrentPosition();
            }
        } else {
            if (mInCallPlayer != null) {
                currPlayPosition = mInCallPlayer.getCurrentPosition();
            }
        }
    }

    public boolean isVoicePlaying() {
        LogUtils.i(TAG,
                "onPlayStatusChnage onSensorPlayModeChanged changePlayMode ******"
                        + (this.mInCallPlayer.isPlaying() || this.mSpeakerPlayer.isPlaying()));
        return this.mInCallPlayer.isPlaying() || this.mSpeakerPlayer.isPlaying();
    }

    /**
     * 停止播放
     */
    public void stopVoice() {
        abandonAudioFocus();
        resetAudioSetting();
        if (mSpeakerPlayer != null) {
            mSpeakerPlayer.stop();
            // should set the state, if not , the following sequence will be
            // error
            // play -> pause -> stop -> resume
            this.mIsPaused = false;
        }

        if (mInCallPlayer != null) {
            mInCallPlayer.stop();
            this.mIsPaused = false;
        }

        if (listener != null) {
            listener.onVoiceStatusChange(mCurrentMusic, STATUS_FINISH);
        }
    }

    /**
     * 暂停播放
     */
    public void pauseVoice() {
        abandonAudioFocus();
        resetAudioSetting();
        if (mSpeakerPlayer != null && mSpeakerPlayer.isPlaying()) {
            mSpeakerPlayer.pause();
            this.mIsPaused = true;
        }
        if (mInCallPlayer != null && mInCallPlayer.isPlaying()) {
            mInCallPlayer.pause();
            this.mIsPaused = true;
        }
        if (listener != null) {
            listener.onVoiceStatusChange(mCurrentMusic, STATUS_FINISH);
        }
    }

    /**
     * 继续播放
     */
    public void resumeVoice() {
        if (mSpeakerPlayer != null && this.mIsPaused) {
            mSpeakerPlayer.start();
            this.mIsPaused = false;
        }
        if (mInCallPlayer != null && this.mIsPaused) {
            mSpeakerPlayer.start();
            this.mIsPaused = false;
        }
        if (listener != null) {
            listener.onVoiceStatusChange(mCurrentMusic, STATUS_PLAY);
        }
    }

    /**
     * 获取音频播放焦点
     */
    private void requestAudioFocus() {

        LogUtils.v(TAG, "requestAudioFocus mAudioFocus = " + mAudioFocus);
        if (!mAudioFocus) {
            int result =
                    audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = true;
            } else {
                LogUtils.e(TAG, "AudioManager request Audio Focus result = " + result);
            }
        }

    }

    /**
     * 放弃音频播放焦点
     */
    private void abandonAudioFocus() {
        LogUtils.v(TAG, "abandonAudioFocus mAudioFocus = " + mAudioFocus);
        if (mAudioFocus) {
            audioManager.abandonAudioFocus(afChangeListener);
            mAudioFocus = false;
        }

    }

}