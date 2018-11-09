package com.johan.mplayer;

import android.app.Application;
import android.content.Context;

import com.johan.async.task.TaskManager;

/**
 * Created by johan on 2018/11/7.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TaskManager.init(this);
        App.context = this;
    }

    public static Context getAppContext() {
        return App.context;
    }

}
