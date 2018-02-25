package com.example.zeners.graduationproject.CallBack;

import com.example.zeners.graduationproject.Disc.widget.DiscView;

/**
 * Created by zouxunxi on 2018/2/16.
 */

public interface IPlayInfo {

    public void onMusicInfoChanged(String musicName, String musicAuthor);

    public void onMusicPicChanged(int musicPic);

    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus);


}
