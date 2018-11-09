package com.johan.mplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.johan.data.module.Video;
import com.johan.mplayer.R;
import com.johan.view.finder.AutoFind;

/**
 * Created by johan on 2018/11/8.
 */

@AutoFind
public class VideoActivity extends BaseActivity <VideoViewFinder> {

    private static final String FIELD_VIDEO = "video_field";

    private Video video;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_video;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            video = savedInstanceState.getParcelable(FIELD_VIDEO);
        } else {
            video = getIntent().getParcelableExtra(FIELD_VIDEO);
        }
        if (video == null) {
            finish();
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        finder.videoView.setVideoPath(video.getPath());
        finder.videoView.setVideoRotation(video.getRotation());
        finder.videoView.play();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void startActivity(Activity activity, Video video) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra(FIELD_VIDEO, video);
        activity.startActivity(intent);
    }

}
