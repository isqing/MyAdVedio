package com.example.admin.myadvedio;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import java.lang.ref.WeakReference;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    private String VIDEO_URL = "https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/orange1.mp4";
    //    private String VIDEO_URL = "http://iflyad.bj.openstorage.cn/dooh/1535873864840.mp4";
//    private String VIDEO_URL = "http://iflyad.bj.openstorage.cn/gnometest/beer/a67e32f7b971eda7e5af08099d6bd3a2.mp4";
    VideoView videoView;
    private boolean isSilent = true;//true开启静音，false关闭静音
    private int currentVoice;
    private final static int TIMEDURATION = 0x123;
    private final static int TIMEOUT = 0x124;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        Log.i("cache==", "onCreate: ");
        initView();
        playVoideo();

    }

    @Override
    protected void onStart() {
        super.onStart();
//            videoView.resume();
        Log.i("position==", "onStart: " + position);
        int time = (videoView.getDuration() - position) / 1000;
        int timeCha = time > 0 ? time : 0;
        videoView.seekTo(position);
        setSeekToPosition();
    }

    private void setSeekToPosition() {
        // 设置 VideoView 的 OnPrepared 监听，拿到 MediaPlayer 对象。
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //设置 MediaPlayer 的 OnSeekComplete 监听
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        // seekTo 方法完成时的回调
                        if (!videoView.isPlaying()) {
                            videoView.start();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        position = videoView.getCurrentPosition();
        if (position > 0) {
            videoView.pause();
        }
        Log.i("position==", "onStop: " + position + "," + videoView.getDuration());
    }

    private void playVoideo() {
        videoView.setVideoPath(VIDEO_URL);
        videoView.start();
    }

    private void initView() {
        videoView = findViewById(R.id.videoView);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.i("setOnPreparedListener", "onPrepared: ");
            }
        });
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_state:
//                videoPlay();
                videoView.start();
                break;
            case R.id.iv_voice:
                if (isSilent) {
                    //关闭声音
                    currentVoice = VoiceUtil.getCurrentVoice(getApplicationContext());
                    VoiceUtil.setVoiceVolume(0, getApplicationContext());
                    isSilent = false;
                } else {
                    //打开声音
                    VoiceUtil.setVoiceVolume(currentVoice, getApplicationContext());
                    isSilent = true;
                }
                break;
            case R.id.iv_close:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

