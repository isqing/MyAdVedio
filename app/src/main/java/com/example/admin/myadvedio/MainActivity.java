package com.example.admin.myadvedio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.admin.myadvedio.cacheVideo.CacheActivity;
import com.example.admin.myadvedio.cacheVideo.CacheVideoDialogFragment;
import com.example.admin.myadvedio.cacheVideo.CacheVideoEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,CacheVideoEventListener{

    Button btnCache,btnCacheOther,btnVideo,btnDialog;
    private CacheVideoDialogFragment cacheVideoDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCache=findViewById(R.id.btn_cache);
        btnCacheOther=findViewById(R.id.btn_cache_other);
        btnCacheOther=findViewById(R.id.btn_cache_other);
        btnVideo=findViewById(R.id.btn_video);
        btnDialog=findViewById(R.id.btn_dialog);
        btnCacheOther.setOnClickListener(this);
        btnCache.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnDialog.setOnClickListener(this);
        cacheVideoDialogFragment = new CacheVideoDialogFragment();
        cacheVideoDialogFragment.setCacheVideoEventListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cache:
                startActivity(new Intent(MainActivity.this,CacheActivity.class));
                break;
            case R.id.btn_cache_other:
                startActivity(new Intent(MainActivity.this,BBVideoPlayer.class));
                break;
            case R.id.btn_video:
                startActivity(new Intent(MainActivity.this,VideoActivity.class));
            case R.id.btn_dialog:
                showEditDialog();
                break;
        }
    }
    public void showEditDialog()
    {
        cacheVideoDialogFragment.show(getSupportFragmentManager(), "EditNameDialog");
    }
    public void hideEditDialog()
    {
        cacheVideoDialogFragment.dismiss();
    }

    @Override
    public void cacheVideoLoadError() {
        Log.i("cacheVideo", "cacheVideoLoadError: ");
    }

    @Override
    public void cacheVideoStart() {
        Log.i("cacheVideo", "cacheVideoStart: ");
    }

    @Override
    public void cacheVideoEnd() {
        Log.i("cacheVideo", "cacheVideoEnd: ");
    }

    @Override
    public void cacheVideoComplete() {
        Log.i("cacheVideo", "cacheVideoComplete: ");
    }
}
