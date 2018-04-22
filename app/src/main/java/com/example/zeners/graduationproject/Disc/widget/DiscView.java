package com.example.zeners.graduationproject.Disc.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.zeners.graduationproject.CallBack.IPlayInfo;
import com.example.zeners.graduationproject.Disc.data.MusicData;
import com.example.zeners.graduationproject.Global.DisplayGlobal;
import com.example.zeners.graduationproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by zouxunxi on 2018/2/15.
 */

public class DiscView extends RelativeLayout {
    private ImageView iv_needle;
    private ViewPager vp_container;
    private ViewPagerAdapter viewPagerAdapter;
    private ObjectAnimator needleAnimator;
    private IPlayInfo iPlayInfo;

    private List<View> discLayouts = new ArrayList<>();
    private List<MusicData> musicDatas = new ArrayList<>();
    private List<ObjectAnimator> discAnimators = new ArrayList<>();

    private boolean isViewPagerOffset = false;
    private boolean isNeed2StartPlayAnimator = false;
    private MusicStatus musicStatus = MusicStatus.STOP;
    private NeedleAnimatorStatus needleAnimatorStatus = NeedleAnimatorStatus.IN_TO_FAR_STILL;
    private int screenWidth = 0;
    private int screenHeight = 0;

    public static final int DURATION_NEEDLE_ANIMATOR = 500;



    public DiscView(Context context) {
        this(context, null);
    }

    public DiscView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public DiscView(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
        screenWidth = DisplayGlobal.getScreenWidth(context);
        screenHeight = DisplayGlobal.getScreenHeight(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initDiscBackground();
        initViewPager();
        initNeedle();
        initObjectAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initDiscBackground() {
        ImageView iv_disc_background = findViewById(R.id.ivDiscBackground);
        iv_disc_background.setImageDrawable(getDiscDrawableBackground() );

        int marginTop = (int) DisplayGlobal.SCALE_DISC_MARGIN_TOP * screenHeight;
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) iv_disc_background.getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0);
        iv_disc_background.setLayoutParams(layoutParams);
    }

    private void initViewPager() {
        viewPagerAdapter = new ViewPagerAdapter();
        vp_container = findViewById(R.id.vpDiscContain);
        vp_container.setOverScrollMode(View.OVER_SCROLL_NEVER);
        vp_container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int lastPositionOffsetPixels = 0;
            int currentItem = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (lastPositionOffsetPixels > positionOffsetPixels) { //左滑
                    if (positionOffset < 0.5) {
                        notifyMusicInfoChanged(position);
                    } else {
                        notifyMusicInfoChanged(vp_container.getCurrentItem() );
                    }
                } else if (lastPositionOffsetPixels < positionOffsetPixels) { //右滑
                    if (positionOffset > 0.5) {
                        notifyMusicInfoChanged(position + 1);
                    } else {
                        notifyMusicInfoChanged(position);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                resetOtherDiscAnimation(position);
                notifyMusicPicChanged(position);
                if (position > currentItem) {
                    notifyMusicStatusChanged(MusicChangedStatus.NEXT);
                } else {
                    notifyMusicStatusChanged(MusicChangedStatus.LAST);
                }
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                setAnimator(state);
            }
        });
        vp_container.setAdapter(viewPagerAdapter);
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) vp_container.getLayoutParams();
        int marginTop = (int) DisplayGlobal.SCALE_DISC_MARGIN_TOP * screenHeight;
        layoutParams.setMargins(0, marginTop, 0, 0);
        vp_container.setLayoutParams(layoutParams);
    }

    //取消其他页面的动画，并将图片旋转角度复原
    private void resetOtherDiscAnimation(int position) {
        for (int i = 0; i < discLayouts.size(); i++) {
            if (position == i) continue;
            discAnimators.get(position).cancel();
            ImageView imageView = discLayouts.get(i).findViewById(R.id.ivDisc);
            imageView.setRotation(0);
        }
    }

    private void setAnimator(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:

            case ViewPager.SCROLL_STATE_SETTLING:
                isViewPagerOffset = false;
                if (musicStatus == MusicStatus.PLAY) playAnimator();
                break;

            case ViewPager.SCROLL_STATE_DRAGGING:
                isViewPagerOffset = true;
                pauseAnimator();
                break;

            default:
                break;
        }
    }

    private void initNeedle() {
        iv_needle = findViewById(R.id.ivNeedle);
        int needleWidth = (int) DisplayGlobal.SCALE_NEEDLE_WIDTH * screenWidth;
        int needleHeight = (int) DisplayGlobal.SCALE_NEEDLE_HEIGHT * screenHeight;

        //设置唱针的外边距为负数，让其隐藏一部分
        int marginTop = (int) DisplayGlobal.SCALE_NEEDLE_MARGIN_TOP * screenHeight * -1;
        int marginLeft = (int) DisplayGlobal.SCALE_NEEDLE_MARGIN_LEFT * screenWidth;

        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_needle);
        Bitmap bitmap = Bitmap.createScaledBitmap(originBitmap, needleWidth, needleHeight, false);

        RelativeLayout.LayoutParams layoutParams = (LayoutParams) iv_needle.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, 0, 0);

        // TODO: 2018/2/16 两个都是width？
        int pivotX = (int) DisplayGlobal.SCALE_NEEDLE_PIVOT_X * screenWidth;
        int pivotY = (int) DisplayGlobal.SCALE_NEEDLE_PIVOT_Y * screenWidth;
        iv_needle.setPivotX(pivotX);
        iv_needle.setPivotY(pivotY);
        iv_needle.setRotation(DisplayGlobal.ROTATION_INIT_NEEDLE);
        iv_needle.setImageBitmap(bitmap);
        iv_needle.setLayoutParams(layoutParams);
    }

    private void initObjectAnimator() {
        needleAnimator = ObjectAnimator.ofFloat(iv_needle, View.ROTATION, DisplayGlobal.ROTATION_INIT_NEEDLE, 0);
        needleAnimator.setDuration(DURATION_NEEDLE_ANIMATOR);
        needleAnimator.setInterpolator(new AccelerateDecelerateInterpolator() );
        needleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                /**
                 * 根据动画开始前NeedleAnimatorStatus的状态
                 * 即可得出动画进行时NeedleAnimatorStatus的状态
                 */
                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_TO_FAR_STILL) {
                    needleAnimatorStatus = NeedleAnimatorStatus.FAR_TO_IN_MOVE;
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.FAR_TO_IN_STILL) {
                    needleAnimatorStatus = NeedleAnimatorStatus.FAR_TO_IN_STILL;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (needleAnimatorStatus == NeedleAnimatorStatus.FAR_TO_IN_MOVE) {
                    needleAnimatorStatus = NeedleAnimatorStatus.FAR_TO_IN_STILL;
                    int index = vp_container.getCurrentItem();
                    playDiscAnimator(index);
                    musicStatus = MusicStatus.PLAY;
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.IN_TO_FAR_MOVE) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_TO_FAR_STILL;
                    if (musicStatus == MusicStatus.STOP) isNeed2StartPlayAnimator = true;
                }

                if (isNeed2StartPlayAnimator) {
                    isNeed2StartPlayAnimator = false;
                    //只有在ViewPager不处于偏移状态时，才开始旋转唱盘
                    if (!isViewPagerOffset) {
                        //延时200ms
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        }, 200);

//                        DiscView.this.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                playAnimator();
//                            }
//                        }, 200);
                    }

                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setPlayInfoListener(IPlayInfo listener) {
        this.iPlayInfo = listener;
    }

    //获得唱盘背后半透明的圆形背景
    private Drawable getDiscDrawableBackground() {
        int discSize = (int) DisplayGlobal.SCALE_DISC_SIZE * screenWidth;
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_disc_blackground),
                discSize, discSize, false);
        return RoundedBitmapDrawableFactory.create(getResources(), bitmapDisc);
    }

    //得到唱盘图片（由空心圆盘及专辑图组合而成）
    private Drawable getDiscDrawable(int musicPicRes) {
        int discSize = (int) DisplayGlobal.SCALE_DISC_SIZE * screenWidth;
        int musicPicSize = (int) DisplayGlobal.SCALE_MUSIC_PIC_SIZE * screenWidth;

        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_disc),
                discSize, discSize, false);
        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize, musicPicRes);
        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapMusicPic);
        //抗锯齿
        discDrawable.setAntiAlias(true);
        roundMusicDrawable.setAntiAlias(true);

        Drawable[] drawables = new Drawable[2];
        drawables[0] = roundMusicDrawable;
        drawables[1] = discDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int musicPicMargin = (int) ((DisplayGlobal.SCALE_DISC_SIZE - DisplayGlobal.SCALE_MUSIC_PIC_SIZE) * screenWidth / 2);
        //调整边距，显示在正中
        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin, musicPicMargin);
        return layerDrawable;
    }

    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) dstSample = sample;
        options.inJustDecodeBounds = false;
        options.inSampleSize = dstSample; //设置图片采样率
        options.inPreferredConfig = Bitmap.Config.RGB_565; //设置图片解码格式
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), musicPicRes, options),
                musicPicRes,musicPicRes, true);

        return bitmap;
    }

    public void setMusicDataList(List<MusicData> musicDataList) {
        if (musicDataList.isEmpty() ) return;
        discLayouts.clear();
        musicDatas.clear();
        discAnimators.clear();
        musicDatas.addAll(musicDataList);

        int i = 0;
        for (MusicData musicData : musicDatas) {
            View discLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc, vp_container, false);
            ImageView iv_disc = discLayout.findViewById(R.id.ivDisc);
            iv_disc.setImageDrawable(getDiscDrawable(musicData.getMusicPic() ) );

            discAnimators.add(getDiscObjectAnimator(iv_disc, i++) );
            discLayouts.add(discLayout);
        }
        viewPagerAdapter.notifyDataSetChanged();

        MusicData musicData = musicDatas.get(0);
        if (iPlayInfo != null) {
            iPlayInfo.onMusicInfoChanged(musicData.getMusicName(), musicData.getMusicAuthor() );
            iPlayInfo.onMusicPicChanged(musicData.getMusicPic() );
        }
    }

    private ObjectAnimator getDiscObjectAnimator(ImageView disc, int i) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(disc, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setInterpolator(new LinearInterpolator() );

        return objectAnimator;
    }

    private void playAnimator() {
        //唱针处于远端时，直接播放动画
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_TO_FAR_STILL) needleAnimator.start();
        //唱针处于往远端方向移动状态时，设置标记，等动画结束后再播放动画
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_TO_FAR_MOVE) isNeed2StartPlayAnimator = true;
    }

    private void pauseAnimator() {
        //播放时，暂停动画
        if (needleAnimatorStatus == NeedleAnimatorStatus.FAR_TO_IN_STILL) {
            int index = vp_container.getCurrentItem();
            pauseDiscAnimator(index);
        }
        //唱针往唱盘移动时，暂停动画
        if (needleAnimatorStatus == NeedleAnimatorStatus.FAR_TO_IN_MOVE) {
            needleAnimator.reverse();
            //若动画在未结束时执行reverse()，则不会执行监听器等onStart(),此时需要手动设置
            needleAnimatorStatus = NeedleAnimatorStatus.IN_TO_FAR_MOVE;
        }
        //动画可能执行多次，只有音乐处于"停止"or"暂停"状态时，才执行暂停命令
        if (musicStatus == MusicStatus.STOP) notifyMusicStatusChanged(MusicChangedStatus.STOP);
        if (musicStatus == MusicStatus.PAUSE) notifyMusicStatusChanged(MusicChangedStatus.PAUSE);

    }

    private void playDiscAnimator(int index) {
        ObjectAnimator objectAnimator = discAnimators.get(index);
        if (objectAnimator.isPaused() ) {
            objectAnimator.resume();
        } else {
            objectAnimator.start();
        }
        //唱盘动画可能执行多次，所以只有当音乐不在播放状态时，再执行播放的回调
        if (musicStatus != MusicStatus.PLAY) notifyMusicStatusChanged(MusicChangedStatus.PLAY);
    }

    private void pauseDiscAnimator(int index) {
        ObjectAnimator objectAnimator = discAnimators.get(index);
        objectAnimator.pause();
        needleAnimator.reverse();
    }

    private void notifyMusicInfoChanged(int position) {
        if (iPlayInfo != null) {
            MusicData musicData = musicDatas.get(position);
            iPlayInfo.onMusicInfoChanged(musicData.getMusicName(), musicData.getMusicAuthor() );
        }
    }

    private void notifyMusicPicChanged(int position) {
        if (iPlayInfo != null) {
            MusicData musicData = musicDatas.get(position);
            iPlayInfo.onMusicPicChanged(musicData.getMusicPic() );
        }
    }

    public void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus) {
        if (iPlayInfo != null) iPlayInfo.onMusicChanged(musicChangedStatus);
    }

    private void play() {
        playAnimator();
    }

    private void pause() {
        musicStatus = MusicStatus.PAUSE;
        pauseAnimator();
    }

    public void stop() {
        musicStatus = MusicStatus.STOP;
        pauseAnimator();
    }

    public void playOrPause() {
        if (musicStatus == MusicStatus.PLAY) {
            pause();
        } else {
            play();
        }
    }

    public void next() {
        int currentItem = vp_container.getCurrentItem();
        if (currentItem == musicDatas.size() - 1) {
            Toast.makeText(getContext(), "已经是最后一首啦！", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            vp_container.setCurrentItem(currentItem + 1, true);
        }
    }

    public void last() {
        int currentItem = vp_container.getCurrentItem();
        if (currentItem == 0) {
            Toast.makeText(getContext(), "你已经到达最后一首啦！", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            vp_container.setCurrentItem(currentItem - 1, true);
        }
    }

    public boolean isPlaying() {
        return musicStatus == MusicStatus.PLAY;
    }

    private void selectMusicWithButton() {
        if (musicStatus == MusicStatus.PLAY) {
            isNeed2StartPlayAnimator = true;
            pauseAnimator();
        }
        if (musicStatus == MusicStatus.PAUSE) {
            play();
        }
    }



    private enum MusicStatus {
        PLAY,
        PAUSE,
        STOP
    }

    private enum NeedleAnimatorStatus {
        IN_TO_FAR_MOVE, //移动时：从唱盘往远处移动
        FAR_TO_IN_MOVE, //移动时：从远处往唱盘移动
        IN_TO_FAR_STILL, //静止时：离开唱盘
        FAR_TO_IN_STILL //静止时：贴近唱盘
    }

    public enum MusicChangedStatus {
        PLAY,
        PAUSE,
        NEXT,
        LAST,
        STOP
    }



    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return discLayouts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View discLayout = discLayouts.get(position);
            container.addView(discLayout);
            return discLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(discLayouts.get(position) );
        }
    }


}
















