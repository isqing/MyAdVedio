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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CacheListener {

    private VideoView videoView;
    private String url="https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/orange1.mp4";
    private int position;
    private final static int TIMEDURATION=0x123;
    private final static int TIMEOUT=0x124;
    private VideoViewHandler videoViewHandler;
    private TextView tvTimer;
    private ImageView ivState,ivVoice;
    private boolean isSilent=true;//true开启静音，false关闭静音
    private int currentVoice=0;
    private long initTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView=findViewById(R.id.videoView);
        tvTimer=findViewById(R.id.tv_timer);
        ivState=findViewById(R.id.iv_state);
        ivVoice=findViewById(R.id.iv_voice);
        videoView.setVideoURI(Uri.parse(url));
        videoViewHandler=new VideoViewHandler(this);
        setListener();
        initTime =System.currentTimeMillis();
        videoViewHandler.sendEmptyMessageDelayed(TIMEDURATION,3000);
        videoPlay();

    }

    private void setListener() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoViewHandler.removeMessages(TIMEOUT);
                //缓存完成
                videoViewHandler.sendEmptyMessageDelayed(TIMEDURATION,1000);

            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ivState.setVisibility(View.VISIBLE);
                ivState.setBackgroundResource(R.mipmap.pause);
            }
        });
        ivState.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivVoice.setBackgroundResource(R.mipmap.voice);
//        VoiceUtil.setVolume(10,getApplicationContext());
    }

    private void videoPlay() {
        HttpProxyCacheServer proxy = MyAppliction.getProxy(getApplicationContext());
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);
        videoView.setVideoPath(proxyUrl);
        tvTimer.setText(String.valueOf(videoView.getDuration()/1000));
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
        Log.i("position==", "onStop: "+position+","+videoView.getDuration());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_state:
//                videoPlay();
                videoView.start();
                ivState.setVisibility(View.GONE);
                break;
            case R.id.iv_voice:
                if (isSilent){
                    //关闭声音
                    currentVoice=VoiceUtil.getCurrentVoice(getApplicationContext());
                    ivVoice.setBackgroundResource(R.mipmap.novoice);
                    VoiceUtil.setVoiceVolume(0,getApplicationContext());
                    isSilent=false;
                }else {
                    //打开声音
                    ivVoice.setBackgroundResource(R.mipmap.voice);
                    VoiceUtil.setVoiceVolume(currentVoice,getApplicationContext());
                    isSilent=true;
                }
                break;

        }
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.i("onCacheAvailable", "onCacheAvailable: "+url+","+percentsAvailable);
    }

    private static class VideoViewHandler extends Handler{
        private WeakReference<Activity> activityWeakReference;
        public VideoViewHandler() {
        }

        public VideoViewHandler(Activity activityWeakReference) {
            this.activityWeakReference = new WeakReference<Activity>(activityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = (MainActivity)activityWeakReference.get();
            if (activity !=null){
                switch (msg.what) {
                    case  TIMEDURATION:
                        MainActivity mainActivity = (MainActivity) activity;
                        VideoView videoView = mainActivity.videoView;
                        int time = (videoView.getDuration() - videoView.getCurrentPosition()) / 1000;
                        mainActivity.tvTimer.setText(String.valueOf(time));
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
    }
}
