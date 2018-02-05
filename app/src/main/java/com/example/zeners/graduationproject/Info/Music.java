package com.example.zeners.graduationproject.Info;

/**
 * Created by Zeners on 2018/2/5.
 */

public class Music {
    private String lyrics;
    private String id;
    private String name;
    private String album;
    private String formats;
    private String artist;

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getId() {
        return id;
    }

    public void setId(String musicId) {
        this.id = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String musicName) {
        this.name = musicName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getFormats() {
        return formats;
    }

    public void setFormats(String formats) {
        this.formats = formats;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }
}
