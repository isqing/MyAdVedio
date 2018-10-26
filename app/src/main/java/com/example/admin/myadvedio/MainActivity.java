package com.example.admin.myadvedio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnCache,btnCacheOther,btnVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCache=findViewById(R.id.btn_cache);
        btnCacheOther=findViewById(R.id.btn_cache_other);
        btnCacheOther=findViewById(R.id.btn_cache_other);
        btnVideo=findViewById(R.id.btn_video);
        btnCacheOther.setOnClickListener(this);
        btnCache.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
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
                break;
        }
    }
}
