package com.example.zeners.graduationproject.Disc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.zeners.graduationproject.Disc.MainDiscActivity;
import com.example.zeners.graduationproject.Disc.data.MusicData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zouxunxi on 2018/2/18.
 */

public class MusicService extends Service {

    //操作指令
    public static final String ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY";
    public static final String ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE";
    public static final String ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT";
    public static final String ACTION_OPT_MUSIC_LAST = "ACTION_OPT_MUSIC_LAST";
    public static final String ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO";

    //状态指令
    public static final String ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY";
    public static final String ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE";
    public static final String ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE";
    public static final String ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION";

    //参数
    public static final String PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION";
    public static final String PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO";
    public static final String PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION";
    public static final String PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER";

    private int currentMusicIndex = 0;
    private boolean isMusicPause = false;

    private List<MusicData> musicDatas = new ArrayList<>();
    private MusicReceiver musicReceiver = new MusicReceiver();
    private MediaPlayer mediaPlayer = new MediaPlayer();




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMusicDatas(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
    }


    private void initMusicDatas(Intent intent) {
        if (intent == null) return;
        List<MusicData> mMusicDatas = (List<MusicData>) intent.getSerializableExtra(MainDiscActivity.PARAM_MUSIC_LIST);
        musicDatas.addAll(mMusicDatas);
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY);
        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE);
        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT);
        intentFilter.addAction(ACTION_OPT_MUSIC_LAST);
        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO);

        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, intentFilter);
    }

    private void play(int index) {
        if (index >= musicDatas.size() ) return;
        if (currentMusicIndex == index && isMusicPause) {
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer = null;

            mediaPlayer = MediaPlayer.create(getApplicationContext(), musicDatas.get(index).getMusicRes() );
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    sendMusicCompleteBradcast();
                }
            });
            currentMusicIndex = index;
            isMusicPause = false;

            int duration = mediaPlayer.getDuration();
            sendMusicDurationBroadcast(duration);
        }
        sendMusicStatusBroadcast(ACTION_STATUS_MUSIC_PLAY);
    }

    private void pause() {
        mediaPlayer.pause();
        isMusicPause = true;
        sendMusicStatusBroadcast(ACTION_STATUS_MUSIC_PAUSE);
    }

    private void stop() {
        mediaPlayer.stop();
    }

    private void next() {
        if (currentMusicIndex + 1 < musicDatas.size() ) {
            play(currentMusicIndex + 1);
        } else {
            stop();
        }
    }

    private void last() {
        if (currentMusicIndex != 0) {
            play(currentMusicIndex - 1);
        }
    }

    private void seekTo(Intent intent) {
        if (mediaPlayer.isPlaying() ) {
            int position = intent.getIntExtra(PARAM_MUSIC_SEEK_TO, 0);
            mediaPlayer.seekTo(position);
        }
    }

    private void sendMusicCompleteBradcast() {
        Intent intent = new Intent(ACTION_STATUS_MUSIC_COMPLETE);
        intent.putExtra(PARAM_MUSIC_IS_OVER, (currentMusicIndex == musicDatas.size() - 1) );
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicDurationBroadcast(int duration) {
        Intent intent = new Intent(ACTION_STATUS_MUSIC_DURATION);
        intent.putExtra(PARAM_MUSIC_DURATION, duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicStatusBroadcast(String action) {
        Intent intent = new Intent(action);
        if (action.equals(ACTION_STATUS_MUSIC_PLAY) ) {
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION, mediaPlayer.getCurrentPosition() );
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }




    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case ACTION_OPT_MUSIC_PLAY:
                    play(currentMusicIndex);
                    break;
                case ACTION_OPT_MUSIC_NEXT:
                    next();
                    break;
                case ACTION_OPT_MUSIC_LAST:
                    last();
                    break;
                case ACTION_OPT_MUSIC_SEEK_TO:
                    seekTo(intent);
                    break;
                default:
                    break;
            }
        }
    }



}
