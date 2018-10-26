package com.example.admin.myadvedio.cacheVideo;

/**
 * create by yqli on 2018/10/26
 */
public interface CacheVideoEventListener {
    public void cacheVideoLoadError();
    public void cacheVideoStart();
    public void cacheVideoEnd();
    public void cacheVideoComplete();
}
