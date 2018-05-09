package com.example.zeners.graduationproject.Info;

import java.util.List;

/**
 * Created by Zeners on 2018/2/5.
 */

public class MusicMeta {
    private String ret;
    private String total;
    private List<Music> musiclist;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Music> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<Music> musiclist) {
        this.musiclist = musiclist;
    }
}
