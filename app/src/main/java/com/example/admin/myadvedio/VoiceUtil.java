package com.example.admin.myadvedio;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * create by yqli on 2018/10/24
 */
public class VoiceUtil {
    public static int getCurrentVoice(Context context){
        AudioManager audioManager=
                (AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return currentVolume;
    }
    /**
     * 使用AudioManager控制音量
     * @param value
     * @param context
     * //    https://github.com/lucid-lynxz/BlogSamples/blob/master/VideoViewDemo/app/src/main/java/org/lynxz/videoviewdemo/MainActivity.java
     */
    public static void setVoiceVolume(float value,Context context) {
        try {
            AudioManager audioManager=
                    (AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i("curr", "setVoiceVolume: "+currentVolume);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//(最大值是15)
//            int flag = value < 0 ? -1 : 1;
            currentVolume = (int)value;
            // 对currentVolume进行限制
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param volume 音量大小
     * @param object VideoView实例
     *   ///VideoView 反射 MediaPlayer控制音量 http://blog.csdn.net/u012874222/article/details/73303264
     * 静音有效果,恢复无效
     * */
    public static void setVolume(float volume,Object object) {
        try {
            Class<?> forName = Class.forName("android.widget.VideoView");
            Field field = forName.getDeclaredField("mMediaPlayer");
            field.setAccessible(true);
            MediaPlayer mMediaPlayer = (MediaPlayer) field.get(object);
            mMediaPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
