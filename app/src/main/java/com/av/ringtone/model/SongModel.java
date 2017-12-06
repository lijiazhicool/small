package com.av.ringtone.model;

import static com.av.ringtone.Constants.FILE_KIND_MUSIC;

import java.io.Serializable;

/**
 * Created by LiJiaZhi on 16/12/19. music
 */

public class SongModel extends VoiceModel {

    public int type = FILE_KIND_MUSIC;
    public long albumId;
    public String albumName;
    public long artistId;
    public long id;
    public int trackNumber;

    public SongModel(String path) {
        this.path = path;
        catorytype = 1;
    }

    public SongModel(String title, String path, int duration, long date) {
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.date = date;

        catorytype = 1;
    }
    public SongModel(long id, long albumId, long artistId, String title, String artistName, String albumName, int duration,
        int trackNumber, String path, long date) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artist = artistName;
        this.duration = duration;
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.path = path;
        this.date = date;

        catorytype = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoiceModel)) return false;

        VoiceModel that = (VoiceModel) o;

        if (catorytype != that.catorytype) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = catorytype;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
