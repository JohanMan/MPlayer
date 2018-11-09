package com.johan.core;

import android.content.Context;

import com.johan.async.task.Task;
import com.johan.data.local.Resolver;
import com.johan.data.module.Video;

import java.util.List;

/**
 * Created by johan on 2018/11/7.
 */

public class VideoCore {

    /**
     * 获取所有视频文件
     * Context 必须用 App Context 不要用 Activity Context 内存泄露问题
     * @param context
     * @param action
     */
    public void loadAllVideo(final Context context, CoreAction<List<Video>> action) {
        Task.Builder<List<Video>> builder = Task.create();
        builder.background(new Task.BackgroundAction<List<Video>>() {
            @Override
            public List<Video> doBackground(Task<List<Video>> task) {
                return Resolver.loadAllVideo(context);
            }
        }).ui(action).error(action).execute();
    }

}
