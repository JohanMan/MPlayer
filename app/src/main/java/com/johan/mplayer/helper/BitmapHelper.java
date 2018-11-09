package com.johan.mplayer.helper;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.LruCache;
import android.widget.ImageView;

import com.johan.async.task.Task;
import com.johan.utils.VideoUtil;

/**
 * Created by johan on 2018/11/8.
 */

public class BitmapHelper {

    /**
     * Bitmap 内存缓存
     */
    private static final int MAX_CACHE_SIZE = 4 * 1024 * 1024;
    private static final LruCache<String, Bitmap> BITMAP_CACHE = new LruCache<String, Bitmap>(MAX_CACHE_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    /**
     * 显示视频图片
     * @param filePath
     * @param placeHolder
     * @param imageView
     */
    public static void showVideoImage(final String filePath, final int placeHolder, final ImageView imageView) {
        imageView.setImageResource(placeHolder);
        Task.Builder<Bitmap> builder = Task.create();
        builder.background(new Task.BackgroundAction<Bitmap>() {
            @Override
            public Bitmap doBackground(Task<Bitmap> task) {
                Bitmap bitmap = BITMAP_CACHE.get(filePath);
                if (bitmap == null) {
                    bitmap = VideoUtil.getVideoThumbNail(filePath);
                    BITMAP_CACHE.put(filePath, bitmap);
                }
                return bitmap;
            }
        }).ui(new Task.UIAction<Bitmap>() {
            @Override
            public void doUI(Bitmap data) {
                if (imageView.getTag().equals(filePath)) {
                    imageView.setImageBitmap(data);
                }
            }
        }).execute();
    }

}
