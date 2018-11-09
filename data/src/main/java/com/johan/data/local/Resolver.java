package com.johan.data.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.johan.data.module.Video;
import com.johan.utils.VideoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johan on 2018/11/6.
 */

public class Resolver {

    /**
     * 获取所有视频
     * @param context
     * @return
     */
    public static List<Video> loadAllVideo(Context context) {
        List<Video> videoList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) return videoList;
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor == null) return videoList;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            long time = new File(path).lastModified();
            long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            VideoUtil.VideoMediaMetadata metadata = VideoUtil.getVideoMediaMetadata(path);
            int rotation = metadata.getRotation();
            int width = metadata.getWidth();
            int height = metadata.getHeight();
            Video video = new Video(id, title, album, path, size, duration, time, rotation, width, height);
            videoList.add(video);
        }
        cursor.close();
        return videoList;
    }

}
