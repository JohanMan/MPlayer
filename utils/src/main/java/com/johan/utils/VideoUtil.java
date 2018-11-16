package com.johan.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by johan on 2018/11/9.
 */

public class VideoUtil {

    /**
     * 获取视频的缩略图
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoThumbNail(String videoPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (videoPath.startsWith("http://")) {
                retriever.setDataSource(videoPath, new HashMap<String, String>());
            } else {
                retriever.setDataSource(videoPath);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取视频的媒体相关信息
     * @param videoPath
     * @return
     */
    public static VideoMediaMetadata getVideoMediaMetadata(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int rotation = 0;
        int width = 0;
        int height = 0;
        try {
            if (videoPath.startsWith("http://")) {
                retriever.setDataSource(videoPath, new HashMap<String, String>());
            } else {
                retriever.setDataSource(videoPath);
            }
            String rotationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String widthString = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String heightString = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (!TextUtils.isEmpty(rotationString)) {
                rotation = Integer.parseInt(rotationString);
            }
            if (!TextUtils.isEmpty(widthString)) {
                width = Integer.parseInt(widthString);
            }
            if (!TextUtils.isEmpty(heightString)) {
                height = Integer.parseInt(heightString);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return new VideoMediaMetadata(rotation, width, height);
    }

    /**
     * 视频的媒体相关信息
     */
    public static class VideoMediaMetadata {

        private int rotation;
        private int width;
        private int height;

        public VideoMediaMetadata(int rotation, int width, int height) {
            this.rotation = rotation;
            this.width = width;
            this.height = height;
        }

        public int getRotation() {
            return rotation;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

}
