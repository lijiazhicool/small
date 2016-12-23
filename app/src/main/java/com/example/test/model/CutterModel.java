package com.example.test.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.test.utils.modelcache.IBaseCacheModel;

/**
 * Created by LiJiaZhi on 16/12/19.
 * cutter
 */

public class CutterModel extends BaseModel implements IBaseCacheModel, Parcelable{
//    public static final int FILE_KIND_MUSIC = 0;
//    public static final int FILE_KIND_ALARM = 1;
//    public static final int FILE_KIND_NOTIFICATION = 2;
//    public static final int FILE_KIND_RINGTONE = 3;
    public  int type;
    public  String title;
    public  String path; // 音乐文件的路径
    public  String artist;
    public int duration;
    public long fileSize;

    public CutterModel(int type, String title, String path, String artisit, int duration, long fileSize) {
        this.type = type;
        this.title = title;
        this.path = path;
        this.artist = artisit;
        this.duration = duration;
        this.fileSize = fileSize;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.path);
        dest.writeString(this.artist);
        dest.writeInt(this.duration);
        dest.writeLong(this.fileSize);
    }

    protected CutterModel(Parcel in) {
        this.type = in.readInt();
        this.title = in.readString();
        this.path = in.readString();
        this.artist = in.readString();
        this.duration = in.readInt();
        this.fileSize = in.readLong();
    }

    public static final Creator<CutterModel> CREATOR = new Creator<CutterModel>() {
        @Override
        public CutterModel createFromParcel(Parcel source) {
            return new CutterModel(source);
        }

        @Override
        public CutterModel[] newArray(int size) {
            return new CutterModel[size];
        }
    };
}
