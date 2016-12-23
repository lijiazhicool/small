package com.example.test.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LiJiaZhi on 16/12/19.
 * music
 */

public class SongModel extends BaseModel implements Parcelable{

    public  long albumId;
    public  String albumName;
    public  long artistId;
    public  String artistName;
    public  int duration;
    public  long id;
    public  String title;
    public  int trackNumber;
    public  String path; // 音乐文件的路径

    public SongModel() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.trackNumber = -1;
        this.path="";
    }

    public SongModel(String title, String path, int duration) {
        this.title = title;
        this.path = path;
        this.duration = duration;
    }

    public SongModel(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber, String _path) {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.path = _path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
