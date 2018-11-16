package com.johan.mplayer;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.danikula.videocache.HttpProxyCacheServer;
import com.johan.async.task.TaskManager;

import java.io.File;

/**
 * Created by johan on 2018/11/7.
 */

public class App extends Application {

    private static Context context;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        TaskManager.init(this);
        App.context = this;
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(100 * 1024 * 1024)
                .cacheDirectory(new File(Environment.getExternalStorageDirectory() + "/MPlayer"))
                .build();
    }

}
