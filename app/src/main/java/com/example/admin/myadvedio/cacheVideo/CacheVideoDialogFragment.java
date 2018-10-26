package com.example.admin.myadvedio.cacheVideo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.admin.myadvedio.MyAppliction;
import com.example.admin.myadvedio.R;
import com.example.admin.myadvedio.VoiceUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * create by yqli on 2018/10/26
 */
public class CacheVideoDialogFragment extends DialogFragment implements CacheListener, View.OnClickListener {
    //        private String VIDEO_URL="https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/orange1.mp4";
//    private String VIDEO_URL = "http://iflyad.bj.openstorage.cn/dooh/1535873864840.mp4";
    private String VIDEO_URL = "http://iflyad.bj.openstorage.cn/gnometest/beer/a67e32f7b971eda7e5af08099d6bd3a2.mp4";
    VideoView videoView;
    private TextView tvTimer;
    private ImageView ivState, ivVoice, ivClose;
    private boolean isSilent = true;//true开启静音，false关闭静音
    private int currentVoice;
    private final static int TIMEDURATION = 0x123;
    private final static int TIMEOUT = 0x124;
    private VideoViewHandler videoViewHandler;
    private HttpProxyCacheServer proxy;
    private int position;
    private ImageView ivLanding;
    private boolean isPlayFinish = false;
    private CacheVideoEventListener cacheVideoEventListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cache, container);
        initView(view);
        playVoideo();
        videoViewHandler = new VideoViewHandler(this);
        videoViewHandler.sendEmptyMessageDelayed(TIMEDURATION, 1000);
        videoViewHandler.sendEmptyMessageDelayed(TIMEOUT, 5000);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//            videoView.resume();
        Log.i("position==", "onStart: " + position);
        int time = (videoView.getDuration() - position) / 1000;
        int timeCha = time > 0 ? time : 0;
        tvTimer.setText(String.valueOf(timeCha));
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
    public void onPause() {
        super.onPause();
        position = videoView.getCurrentPosition();
        if (position > 0) {
            videoView.pause();
        }
        Log.i("position==", "onStop: " + position + "," + videoView.getDuration());
    }

    private void playVoideo() {
        videoView.setVideoURI(Uri.parse(VIDEO_URL));
        proxy = MyAppliction.getProxy(getContext());
        proxy.registerCacheListener(this, VIDEO_URL);
        String proxyUrl = proxy.getProxyUrl(VIDEO_URL);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }

    private void initView(View view) {
        videoView = view.findViewById(R.id.videoView);
        tvTimer = view.findViewById(R.id.tv_timer);
        ivState = view.findViewById(R.id.iv_state);
        ivVoice = view.findViewById(R.id.iv_voice);
        ivClose = view.findViewById(R.id.iv_close);
        ivLanding = view.findViewById(R.id.iv_landing);
        ivState.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivVoice.setBackgroundResource(R.mipmap.voice);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ivState.setVisibility(View.VISIBLE);
                ivState.setBackgroundResource(R.mipmap.pause);
                ivLanding.setVisibility(View.VISIBLE);
//                videoView.setVisibility(View.INVISIBLE);
                ivLanding.setBackgroundResource(R.drawable.lating);
                ivClose.setVisibility(View.VISIBLE);
                isPlayFinish = true;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.i("setOnPreparedListener", "onPrepared: ");
                ivLanding.setVisibility(View.GONE);
                Log.i("cache===", "handleMessage: removeMessages");
                videoViewHandler.removeMessages(TIMEOUT);
                if (cacheVideoEventListener != null) {
                    cacheVideoEventListener.cacheVideoStart();
                }
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                if (cacheVideoEventListener != null) {
                    cacheVideoEventListener.cacheVideoLoadError();
                }
                return false;
            }
        });
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.i("cache==", String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, cacheFile, url));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_state:
//                videoPlay();
                videoView.start();
//                videoView.setVisibility(View.VISIBLE);
                ivLanding.setVisibility(View.GONE);
                ivState.setVisibility(View.GONE);
                break;
            case R.id.iv_voice:
                if (isSilent) {
                    //关闭声音
                    currentVoice = VoiceUtil.getCurrentVoice(getContext().getApplicationContext());
                    ivVoice.setBackgroundResource(R.mipmap.novoice);
                    VoiceUtil.setVoiceVolume(0, getContext().getApplicationContext());
                    isSilent = false;
                } else {
                    //打开声音
                    ivVoice.setBackgroundResource(R.mipmap.voice);
                    VoiceUtil.setVoiceVolume(currentVoice, getContext().getApplicationContext());
                    isSilent = true;
                }
                break;
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.videoView:
                break;
            case R.id.iv_landing:

                break;
        }
    }

    private static class VideoViewHandler extends Handler {
        private WeakReference<DialogFragment> dialogFragmentWeakReference;

        public VideoViewHandler() {
        }

        public VideoViewHandler(DialogFragment dialogFragment) {
            this.dialogFragmentWeakReference = new WeakReference<DialogFragment>(dialogFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CacheVideoDialogFragment cacheVideoDialogFragment = (CacheVideoDialogFragment) dialogFragmentWeakReference.get();
            if (cacheVideoDialogFragment != null) {
                switch (msg.what) {
                    case TIMEDURATION:
                        VideoView videoView = cacheVideoDialogFragment.videoView;
                        int videoViewDuration = videoView.getDuration();
                        int time = (videoViewDuration - videoView.getCurrentPosition()) / 1000;
                        if (time==videoViewDuration/1000){
                            removeMessages(TIMEOUT);
                            CacheVideoEventListener listener = cacheVideoDialogFragment.cacheVideoEventListener;
                            if (listener != null) {
                                listener.cacheVideoStart();
                            }
                        }
                        cacheVideoDialogFragment.tvTimer.setText(String.valueOf(time));
                        sendEmptyMessageDelayed(TIMEDURATION, 1000);
                        break;
                    case TIMEOUT:
                        Log.i("cache===", "handleMessage: timeout");
                        cacheVideoDialogFragment.ivClose.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoViewHandler != null) {
            videoViewHandler.removeCallbacksAndMessages(null);
            videoViewHandler = null;
        }
        if (proxy != null) {
            proxy.unregisterCacheListener(this, VIDEO_URL);
        }
        if (cacheVideoEventListener != null) {
            if (isPlayFinish) {
                cacheVideoEventListener.cacheVideoComplete();
            } else {
                cacheVideoEventListener.cacheVideoEnd();
            }
        }
    }

    public void setCacheVideoEventListener(CacheVideoEventListener cacheVideoEventListener) {
        this.cacheVideoEventListener = cacheVideoEventListener;
    }
}

