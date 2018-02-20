package com.example.zeners.graduationproject.Global;

import android.content.Context;

/**
 * Created by zouxunxi on 2018/2/16.
 */

public class DisplayGlobal {
    public static final float ROTATION_INIT_NEEDLE = -30; //手柄起始角度
    public static final float BASE_SCREEN_WIDTH = 1080; //截图屏幕宽度
    public static final float BASE_SCREEN_HEIGHT = 1920; //截图屏幕高度
    public static final float SCALE_NEEDLE_WIDTH = 276 / BASE_SCREEN_WIDTH; //唱针宽度
    public static final float SCALE_NEEDLE_HEIGHT = 413 / BASE_SCREEN_HEIGHT; //唱针高度
    public static final float SCALE_NEEDLE_MARGIN_LEFT = 500 / BASE_SCREEN_WIDTH; //唱针左边距
    public static final float SCALE_NEEDLE_MARGIN_TOP = 43 /BASE_SCREEN_HEIGHT; //唱针顶边距
    public static final float SCALE_NEEDLE_PIVOT_X = 43 / BASE_SCREEN_WIDTH; //
    public static final float SCALE_NEEDLE_PIVOT_Y = 43 / BASE_SCREEN_WIDTH;

    public static final float SCALE_DISC_SIZE = 813 / BASE_SCREEN_WIDTH; //唱盘尺寸比例
    public static final float SCALE_DISC_MARGIN_TOP = 190 / BASE_SCREEN_HEIGHT; //唱盘顶部边距

    public static final float SCALE_MUSIC_PIC_SIZE = 533 / BASE_SCREEN_WIDTH; //专辑图片比例

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
