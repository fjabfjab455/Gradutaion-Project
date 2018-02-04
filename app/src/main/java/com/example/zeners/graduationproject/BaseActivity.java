package com.example.zeners.graduationproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Zeners on 2018/2/3.
 */

public class BaseActivity extends AppCompatActivity{
    private static final String TAG = "BaseActivity";

    protected Context context;
    protected boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initData(savedInstanceState);
        initContentView();
        findViews();
        setListeners();
        initViews();
    }

    protected void initData(Bundle savedInstanceState) { }

    protected void initContentView() { }

    protected void findViews() { }

    protected void setListeners() { }

    protected void initViews() { }

    public View getContentView() {
        return findViewById(android.R.id.content);
    }

    public Class getNextActivity() {
        return null;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void onResume() {
        active = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        active = false;
        super.onPause();
    }

}
