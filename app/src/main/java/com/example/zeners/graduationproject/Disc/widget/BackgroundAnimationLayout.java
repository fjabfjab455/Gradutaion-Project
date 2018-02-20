package com.example.zeners.graduationproject.Disc.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.example.zeners.graduationproject.R;

/**
 * Created by zouxunxi on 2018/2/17.
 */

public class BackgroundAnimationLayout extends RelativeLayout {
    private final int DURATION_ANIMATION = 500;
    private final int INDEX_BACKGROUND = 0;
    private final int INDEX_FOREGROUND = 1;
    /**
     * LayerDrawable[0]: background drawable
     * LayerDrwable[1]: foreground drawable
     */
    private LayerDrawable layerDrawable;
    private ObjectAnimator objectAnimator;
    private int musicPicRes = -1;


    public BackgroundAnimationLayout(Context context) {
        this(context, null);
    }

    public BackgroundAnimationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundAnimationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayerDrawable();
        initObjectAnimator();
    }

    private void initLayerDrawable() {
        Drawable backgroundDrawable = getContext().getDrawable(R.drawable.ic_background);
        Drawable[] drawables = new Drawable[2];
        //初始化时，先将前景与背景颜色设为一致
        drawables[INDEX_BACKGROUND] = backgroundDrawable;
        drawables[INDEX_FOREGROUND] = backgroundDrawable;

        layerDrawable = new LayerDrawable(drawables);
    }

    private void initObjectAnimator() {
        objectAnimator = ObjectAnimator.ofFloat(this, "number", 0f, 1.0f);
        objectAnimator.setDuration(DURATION_ANIMATION);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator() );
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int foregroundAlpha = (int) valueAnimator.getAnimatedValue() * 255;
                //动态设置Drawable的透明度，让前景图逐渐显示
                layerDrawable.getDrawable(INDEX_FOREGROUND).setAlpha(foregroundAlpha);
                BackgroundAnimationLayout.this.setBackground(layerDrawable);
            }
        });
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //动画结束后，将原来的背景图及时更新
                layerDrawable.setDrawable(INDEX_BACKGROUND, layerDrawable.getDrawable(INDEX_FOREGROUND) );
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setForeground(Drawable drawable) {
        layerDrawable.setDrawable(INDEX_FOREGROUND, drawable);
    }

    //开始渐变动画
    public void beginAnimation() {
        objectAnimator.start();
    }

    public boolean isNeed2UpdateBackground(int musicPicRes) {
        if (this.musicPicRes == -1) return true;
        if (this.musicPicRes != musicPicRes) return true;
        return false;
    }















}
