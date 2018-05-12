package com.example.zeners.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zeners.graduationproject.Service.PlayService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by Zeners on 2018/2/5.
 */

public class PlayAcitivty extends BaseActivity {
    private static final String TAG = "PlayActivity";
    private Button btn_play_net_audio, btn_stop_net_audio;
    private TextView tv_name;
    private String id, name, url;
    private Toolbar toolbar;
    private AsyncHttpClient client;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        client = new AsyncHttpClient();
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        setContentView(R.layout.activity_play);
    }

    @Override
    protected void findViews() {
        super.findViews();
        toolbar = findViewById(R.id.toolbar);
        tv_name = findViewById(R.id.tv_name);
        btn_play_net_audio = findViewById(R.id.btn_play_net_audio);
        btn_stop_net_audio = findViewById(R.id.btn_stop_net_audio);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                Log.w(TAG, "onClick: finish? ");
            }
        });

        btn_play_net_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2018/2/5 从点击一次创建一个新对象 改为 全局变量，点一次get一次，最好测下有没有什么问题？
                client.get("http://antiserver.kuwo.cn/anti.s?type=convert_url&rid=MUSIC_" + id + "&format=mp3&response=url",
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == HttpStatus.SC_OK) {
                                    url = new String(responseBody);
                                    Log.w(TAG, "onSuccess: " + url );
                                    Intent intent = new Intent(context, PlayService.class);
                                    intent.putExtra("URL", url);
                                    startService(intent);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "*** failed to get url of the audio! ***");
                            }
                        });
            }
        });

        btn_stop_net_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(context, PlayService.class));
            }
        });



    }

    @Override
    protected void initViews() {
        super.initViews();
        tv_name.setText(name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置返回键
        getSupportActionBar().setHomeButtonEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getWindow().setStatusBarColor(Color.parseColor("#303F9F") );

        setTitle("播放界面");
    }
}
