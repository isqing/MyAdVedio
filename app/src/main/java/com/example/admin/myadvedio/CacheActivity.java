package com.example.admin.myadvedio;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;
import java.lang.ref.WeakReference;

public class CacheActivity extends AppCompatActivity implements CacheListener ,View.OnClickListener{
    private String VIDEO_URL="https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/orange1.mp4";
    VideoView videoView;
    private TextView tvTimer;
    private ImageView ivState,ivVoice,ivClose;
    private boolean isSilent=true;//true开启静音，false关闭静音
    private int currentVoice;
    private final static int TIMEDURATION=0x123;
    private final static int TIMEOUT=0x124;
    private VideoViewHandler videoViewHandler;
    private HttpProxyCacheServer proxy;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        Log.i("cache==", "onCreate: ");
        initView();
        playVoideo();
        videoViewHandler=new VideoViewHandler(this);
        videoViewHandler.sendEmptyMessageDelayed(TIMEDURATION,1000);
//        videoViewHandler.sendEmptyMessageDelayed(TIMEOUT,3000);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (position!=0) {
//            videoView.resume();
            Log.i("position==", "onResume: "+position);
            videoView.seekTo(position);

            setSeekToPosition();

        }
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
                        if(!videoView.isPlaying()){
                            videoView.start();
                            int time = (videoView.getDuration() - position) / 1000;
                            tvTimer.setText(String.valueOf(time));
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
        videoView.pause();
        Log.i("position==", "onStop: "+ position +","+videoView.getDuration());
    }
    private void playVoideo() {
        videoView.setVideoURI(Uri.parse(VIDEO_URL));
        proxy = MyAppliction.getProxy(getApplicationContext());
        proxy.registerCacheListener(this, VIDEO_URL);
        String proxyUrl = proxy.getProxyUrl(VIDEO_URL);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }

    private void initView() {
        videoView=findViewById(R.id.videoView);
        tvTimer=findViewById(R.id.tv_timer);
        ivState=findViewById(R.id.iv_state);
        ivVoice=findViewById(R.id.iv_voice);
        ivClose=findViewById(R.id.iv_close);
        ivState.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivVoice.setBackgroundResource(R.mipmap.voice);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ivState.setVisibility(View.VISIBLE);
                ivState.setBackgroundResource(R.mipmap.pause);
                ivClose.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.i("cache==", String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, cacheFile, url));

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
                ivState.setVisibility(View.GONE);
                break;
            case R.id.iv_voice:
                if (isSilent) {
                    //关闭声音
                    currentVoice = VoiceUtil.getCurrentVoice(getApplicationContext());
                    ivVoice.setBackgroundResource(R.mipmap.novoice);
                    VoiceUtil.setVoiceVolume(0, getApplicationContext());
                    isSilent = false;
                } else {
                    //打开声音
                    ivVoice.setBackgroundResource(R.mipmap.voice);
                    VoiceUtil.setVoiceVolume(currentVoice, getApplicationContext());
                    isSilent = true;
                }
                break;
                case R.id.iv_close:
                    finish();
                    break;
        }
    }
    private static class VideoViewHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;
        public VideoViewHandler() {
        }

        public VideoViewHandler(Activity activityWeakReference) {
            this.activityWeakReference = new WeakReference<Activity>(activityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CacheActivity activity = (CacheActivity)activityWeakReference.get();
            if (activity !=null){
                switch (msg.what) {
                    case  TIMEDURATION:
                        CacheActivity cacheActivity = (CacheActivity) activity;
                        VideoView videoView = cacheActivity.videoView;
                        int time = (videoView.getDuration() - videoView.getCurrentPosition()) / 1000;
                        cacheActivity.tvTimer.setText(String.valueOf(time));
                        sendEmptyMessageDelayed(TIMEDURATION, 1000);
                        break;
                    case  TIMEOUT:
                        activity.finish();
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoViewHandler!=null){
            videoViewHandler.removeCallbacksAndMessages(null);
            videoViewHandler=null;
        }
        if (proxy!=null){
            proxy.unregisterCacheListener(this,VIDEO_URL);
        }
    }
}
