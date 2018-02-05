package com.example.zeners.graduationproject.Info;

import java.util.List;

/**
 * Created by Zeners on 2018/2/5.
 */

public class MusicMeta {
    private String rest;
    private String total;
    private List<Music> musicList;

    public void setRest(String rest) {
        this.rest = rest;
    }

    public String getRest() {
        return rest;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public List<Music> getMusicList() {
        return musicList;
    }
}
