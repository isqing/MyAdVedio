package com.example.admin.myadvedio;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * create by yqli on 2018/10/25
 */
public class MyAppliction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyAppliction app = (MyAppliction) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Utils.getVideoCacheDir(this))
                .maxCacheSize(20 * 1024)       // 10 m for cache
                .build();
    }
}
