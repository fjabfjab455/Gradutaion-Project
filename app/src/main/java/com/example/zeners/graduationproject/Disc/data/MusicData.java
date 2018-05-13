package com.example.zeners.graduationproject.Disc.data;

import com.example.zeners.graduationproject.R;

import java.io.Serializable;

/**
 * Created by zouxunxi on 2018/2/15.
 */

public class MusicData implements Serializable {
    private int musicRes; // 音乐资源
    private int musicPic; // 专辑图片
    private String musicName; // 音乐名称
    private String musicAuthor; // 作者
    private String url; // 播放地址

    public MusicData(int musicRes, int musicPic, String musicName, String musicAuthor) {
        this.musicRes = musicRes;
        this.musicPic = musicPic;
        this.musicName = musicName;
        this.musicAuthor = musicAuthor;
    }

    public MusicData(String url, String musicName) {
        this.url = url;
        this.musicName = musicName;
        this.musicPic = R.raw.ic_music_1;
        this.musicAuthor = "testAuthor";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setMusicRes(int musicRes) {
        this.musicRes = musicRes;
    }

    public int getMusicRes() {
        return musicRes;
    }

    public void setMusicPic(int musicPic) {
        this.musicPic = musicPic;
    }

    public int getMusicPic() {
        return musicPic;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }
}
