package com.example.test.model;

import android.os.Parcel;
import android.os.Parcelable;

import static com.example.test.Constants.FILE_KIND_MUSIC;

/**
 * Created by LiJiaZhi on 16/12/19. music
 */

public class SongModel extends BaseModel implements Parcelable {

    public int type = FILE_KIND_MUSIC;
    public long albumId;
    public String albumName;
    public long artistId;
    public String artistName;
    public int duration;
    public long id;
    public String title;
    public int trackNumber;
    public String path; // 音乐文件的路径

    public SongModel(String title, String path, int duration) {
        this.title = title;
        this.path = path;
        this.duration = duration;
    }
    public SongModel(long id, long albumId, long artistId, String title, String artistName, String albumName, int duration,
        int trackNumber, String path) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
        this.duration = duration;
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeLong(this.albumId);
        dest.writeString(this.albumName);
        dest.writeLong(this.artistId);
        dest.writeString(this.artistName);
        dest.writeInt(this.duration);
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.trackNumber);
        dest.writeString(this.path);
    }

    protected SongModel(Parcel in) {
        this.type = in.readInt();
        this.albumId = in.readLong();
        this.albumName = in.readString();
        this.artistId = in.readLong();
        this.artistName = in.readString();
        this.duration = in.readInt();
        this.id = in.readLong();
        this.title = in.readString();
        this.trackNumber = in.readInt();
        this.path = in.readString();
    }

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel source) {
            return new SongModel(source);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };
}
