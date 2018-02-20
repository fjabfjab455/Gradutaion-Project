package com.example.zeners.graduationproject.Disc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.zeners.graduationproject.BaseActivity;
import com.example.zeners.graduationproject.CallBack.IPlayInfo;
import com.example.zeners.graduationproject.Disc.data.MusicData;
import com.example.zeners.graduationproject.Disc.service.MusicService;
import com.example.zeners.graduationproject.Disc.widget.BackgroundAnimationLayout;
import com.example.zeners.graduationproject.Disc.widget.DiscView;
import com.example.zeners.graduationproject.Global.DisplayGlobal;
import com.example.zeners.graduationproject.R;
import com.example.zeners.graduationproject.Utils.FastBlurUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zouxunxi on 2018/2/15.
 */

public class MainDiscActivity extends BaseActivity {

    private DiscView discView;
    private Toolbar toolbar;
    private SeekBar seekBar;
    private ImageView iv_playOrPause, iv_next, iv_last;
    private TextView tv_musicDuration, tv_totalMusicDuration;
    private BackgroundAnimationLayout rootLayout;
    private MusicReceiver musicReceiver;
    private List<MusicData> musicDatas;

    public static final int MUSIC_MESSAGE = 0;
    public static final String PARAM_MUSIC_LIST = "PARAM_MUSIC_LIST";



    private Handler musicHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            seekBar.setProgress(seekBar.getProgress() + 1000);
            tv_musicDuration.setText(duration2Text(seekBar.getProgress() ) );
            startUpdateSeekBarProgress();
            return false;
        }
    });
    // FIXME: 2018/2/20 原来是这个方法，但会造成内存泄露，修改了一下，试试看能不能行
//    private  Handler musicHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            seekBar.setProgress(seekBar.getProgress() + 1000);
//            tv_musicDuration.setText(duration2Text(seekBar.getProgress() ) );
//            startUpdateSeekBarProgress();
//        }
//    };



    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        musicReceiver = new MusicReceiver();
        musicDatas = new ArrayList<>();
        initMusicDatas();
        initMusicReceiver();

    }

    @Override
    protected void initContentView() {
        super.initContentView();
        setContentView(R.layout.activity_disc_main);

    }

    @Override
    protected void findViews() {
        super.findViews();
        discView = findViewById(R.id.discview);
        iv_next = findViewById(R.id.ivNext);
        iv_last = findViewById(R.id.ivLast);
        iv_playOrPause = findViewById(R.id.ivPlayOrPause);
        tv_musicDuration = findViewById(R.id.tvCurrentTime);
        tv_totalMusicDuration = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.musicSeekBar);
        rootLayout = findViewById(R.id.rootLayout);
        toolbar = findViewById(R.id.toolBar);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        discView.setPlayInfoListener(new IPlayInfo() {
            @Override
            public void onMusicInfoChanged(String musicName, String musicAuthor) {
                try {
                    getSupportActionBar().setTitle(musicName);
                    getSupportActionBar().setSubtitle(musicAuthor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMusicPicChanged(int musicPic) {
                updateMusicPicInBackground(musicPic);
            }

            @Override
            public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {
                switch (musicChangedStatus) {
                    case PLAY:
                        play();
                        break;
                    case PAUSE:
                        pause();
                        break;
                    case NEXT:
                        next();
                        break;
                    case LAST:
                        last();
                        break;
                    case STOP:
                        stop();
                        break;
                    default:
                        break;
                }
            }
        });

        iv_playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discView.playOrPause();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discView.next();
            }
        });

        iv_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discView.last();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_musicDuration.setText(duration2Text(i) ); //i: progress b: isFromUser
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdateSeekBarProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress() );
                startUpdateSeekBarProgress();
            }
        });


    }

    @Override
    protected void initViews() {
        super.initViews();
        setSupportActionBar(toolbar);
        tv_musicDuration.setText(duration2Text(0) );
        tv_totalMusicDuration.setText(duration2Text(0) );
        discView.setMusicDataList(musicDatas);
        makeStatusBarTransparent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
    }

    private void initMusicDatas() {
        MusicData musicData_1 = new MusicData(R.raw.music_1, R.raw.ic_music_1, "寻", "三亩地");
        MusicData musicData_2 = new MusicData(R.raw.music_2, R.raw.ic_music_2, "Nighting", "YANI");
        MusicData musicData_3 = new MusicData(R.raw.music_3, R.raw.ic_music_3, "Confield Chase", "Hans Zimmer");

        musicDatas.add(musicData_1);
        musicDatas.add(musicData_2);
        musicDatas.add(musicData_3);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST, (Serializable) musicDatas);
        startService(intent);
    }

    private void initMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, intentFilter);
    }

    //设置状态栏为透明
    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void updateMusicPicInBackground(final int musicPicRes) {
        if (rootLayout.isNeed2UpdateBackground(musicPicRes) ) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Drawable foregroundDrawable = getForegroundDrawable(musicPicRes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rootLayout.setForeground(foregroundDrawable);
                            rootLayout.beginAnimation();
                        }
                    });
                }
            }).start();
        }
    }

    private Drawable getForegroundDrawable(int musicPicRes) {
        //得到屏幕的宽高比，以便按比例切割图片一部分
        float widthHeightSize = (float) DisplayGlobal.getScreenWidth(this) / DisplayGlobal.getScreenHeight(this);
        Bitmap bitmap = getForegroundBitmap(musicPicRes);
        int cropBitmapWidth = (int) widthHeightSize * bitmap.getHeight();
        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);
        //切割部分图片
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth, bitmap.getHeight() );
        //缩小图片
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap.getHeight() / 50, false);
        //模糊化
        Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

        // FIXME: 2018/2/20 原来的方法是new BitmapDrawable(blurBitmap),但官方不建议用了
        Drawable foregroundDrawable = new BitmapDrawable(getResources(), blurBitmap);
        //加入灰色蒙板，避免图片过亮而影响其他控件
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }


    private Bitmap getForegroundBitmap(int musicPicRes) {
        int screenWidth = DisplayGlobal.getScreenWidth(this);
        int screenHeight = DisplayGlobal.getScreenHeight(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        if (imageWidth < screenWidth && imageHeight < screenHeight) return BitmapFactory.decodeResource(getResources(), musicPicRes);

        int sample = 2;
        int sampleX = imageWidth / DisplayGlobal.getScreenWidth(this);
        int sampleY = imageHeight / DisplayGlobal.getScreenHeight(this);
        if (sampleX > sampleY && sampleY > 1) {
            sample = sampleX;
        } else if (sampleY > sampleX && sampleX > 1) {
            sample = sampleY;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeResource(getResources(), musicPicRes, options);
    }

    private void play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
        startUpdateSeekBarProgress();
    }

    private void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
        stopUpdateSeekBarProgress();
    }

    private void stop() {
        stopUpdateSeekBarProgress();
        iv_playOrPause.setImageResource(R.drawable.ic_play);
        tv_musicDuration.setText(duration2Text(0) );
        tv_totalMusicDuration.setText(duration2Text(0) );
        seekBar.setProgress(0);
    }

    private void next() {
        rootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        }, DiscView.DURATION_NEEDLE_ANIMATOR);
        stopUpdateSeekBarProgress();
        tv_musicDuration.setText(duration2Text(0) );
        tv_totalMusicDuration.setText(duration2Text(0) );
    }

    private void last() {
        rootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
            }
        }, DiscView.DURATION_NEEDLE_ANIMATOR);
        stopUpdateSeekBarProgress();
        tv_musicDuration.setText(duration2Text(0) );
        tv_totalMusicDuration.setText(duration2Text(0) );
    }

    private void complete(boolean isOver) {
        if (isOver) {
            discView.stop();
        } else {
            discView.next();
        }
    }

    private void optMusic(String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action) );
    }

    private void seekTo(int position) {
        Intent intent = new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO, position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //根据时长转成时间文本格式
    private String duration2Text(int duration) {
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        return (min < 10 ? "0" + min : String.valueOf(min) ) + ":" + (sec < 10 ? "0" + sec : String.valueOf(sec) );
    }

    private void startUpdateSeekBarProgress() {
        //避免重复发送Message
        stopUpdateSeekBarProgress();
        musicHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void stopUpdateSeekBarProgress() {
        musicHandler.removeMessages(MUSIC_MESSAGE);
    }

    private void updateMusicDurationInfo(int totalDuration) {
        seekBar.setProgress(0);
        seekBar.setMax(totalDuration);
        tv_totalMusicDuration.setText(duration2Text(totalDuration) );
        tv_musicDuration.setText(duration2Text(0) );
        startUpdateSeekBarProgress();
    }


    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case MusicService.ACTION_STATUS_MUSIC_PLAY:
                    iv_playOrPause.setImageResource(R.drawable.ic_pause);
                    int currentPosition = intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION, 0);
                    seekBar.setProgress(currentPosition);
                    if (!discView.isPlaying() ) discView.playOrPause(); //开始播放
                    break;

                case MusicService.ACTION_STATUS_MUSIC_PAUSE:
                    iv_playOrPause.setImageResource(R.drawable.ic_play);
                    if (discView.isPlaying() ) discView.playOrPause(); //暂停播放
                    break;

                case MusicService.ACTION_STATUS_MUSIC_DURATION:
                    int duration = intent.getIntExtra(MusicService.PARAM_MUSIC_DURATION, 0);
                    updateMusicDurationInfo(duration);
                    break;

                case MusicService.ACTION_STATUS_MUSIC_COMPLETE:
                    boolean isOver = intent.getBooleanExtra(MusicService.PARAM_MUSIC_IS_OVER, true);
                    complete(isOver);
                    break;

                default:
                    break;
            }
        }
    }




}
