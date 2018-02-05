package com.example.zeners.graduationproject.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Zeners on 2018/2/5.
 */

public class PlayService extends Service {
    private static final String TAG = "PlayService";
    private MediaPlayer player;
    private String currentPlay; //当前播放的文件



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        currentPlay = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "*** start command ***");
        String url = intent.getStringExtra("URL");
        if (url == null || url.isEmpty() || url.equals(currentPlay) ) {
            if (player.isPlaying() ) {
                player.pause();
            } else {
                player.start();
            }

        } else {
            player.reset();
            try {
                player.setDataSource(url);
                player.prepare();
                player.start();
                currentPlay = url;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }
}
