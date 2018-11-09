package com.johan.data.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johan on 2018/11/6.
 */

public class Video implements Parcelable {

    private int id;
    private String title;
    private String album;
    private String path;
    private long size;
    private long duration;
    private long time;
    private int rotation;
    private int width;
    private int height;

    public Video() {

    }

    public Video(int id, String title, String album, String path, long size, long duration, long time, int rotation, int width, int height) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.path = path;
        this.size = size;
        this.duration = duration;
        this.time = time;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeLong(time);
        dest.writeInt(rotation);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            Video video = new Video();
            video.id = source.readInt();
            video.title = source.readString();
            video.album = source.readString();
            video.path = source.readString();
            video.size = source.readLong();
            video.duration = source.readLong();
            video.time = source.readLong();
            video.rotation = source.readInt();
            video.width = source.readInt();
            video.height = source.readInt();
            return video;
        }
        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

}
