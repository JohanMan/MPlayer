package com.johan.mplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.danikula.videocache.HttpProxyCacheServer;
import com.johan.async.task.Task;
import com.johan.data.module.Video;
import com.johan.mplayer.App;
import com.johan.mplayer.R;
import com.johan.utils.VideoUtil;
import com.johan.view.finder.AutoFind;

import java.util.ArrayList;

import tv.johan.player.AndroidMediaController;
import tv.johan.player.VideoHelper;

/**
 * Created by johan on 2018/11/8.
 */

@AutoFind
public class VideoActivity extends BaseActivity <VideoViewFinder> implements AndroidMediaController.OnClickBackListener,
        AndroidMediaController.OnClickRotationListener, AndroidMediaController.OnClickNextListener {

    private static final String FIELD_VIDEO = "video_field";
    private static final String FIELD_INDEX = "index_field";
    private static final String FIELD_LIVE = "live_field";

    private ArrayList<Video> videoList;
    private int index;
    private boolean isLive;
    private AndroidMediaController mediaController;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_video;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            videoList = savedInstanceState.getParcelableArrayList(FIELD_VIDEO);
            index = savedInstanceState.getInt(FIELD_INDEX);
            isLive = savedInstanceState.getBoolean(FIELD_LIVE);
        } else {
            videoList = getIntent().getParcelableArrayListExtra(FIELD_VIDEO);
            index = getIntent().getIntExtra(FIELD_INDEX, 0);
            isLive = getIntent().getBooleanExtra(FIELD_LIVE, false);
        }
        if (videoList == null) {
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
        Video video = videoList.get(index);
        if (VideoHelper.isWidescreenVideo(video.getWidth(), video.getHeight(), video.getRotation())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                initVideo();
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(FIELD_VIDEO, videoList);
        outState.putInt(FIELD_INDEX, index);
        outState.putBoolean(FIELD_LIVE, isLive);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        finder.videoView.release();
        mediaController.release();
        super.onDestroy();
    }

    private void initVideo() {
        Video video = videoList.get(index);
        if (VideoHelper.isWidescreenVideo(video.getWidth(), video.getHeight(), video.getRotation())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (mediaController == null) {
            mediaController = new AndroidMediaController(this);
            mediaController.setOnClickBackListener(this);
            mediaController.setOnClickRotationListener(this);
            mediaController.setOnClickNextListener(this);
            finder.videoView.setMediaController(mediaController);
        }
        mediaController.reset();
        mediaController.canNext(videoList.size() - 1 != index);
        mediaController.showProgress(!isLive);
        mediaController.canSpeed(!isLive);
        mediaController.setVideoName(video.getTitle());
        HttpProxyCacheServer proxy = App.getProxy(this);
        String url = video.getPath();
        if (!isLive) {
            url = proxy.getProxyUrl(url);
        }
        finder.videoView.setVideoPath(url);
        finder.videoView.setVideoRotation(video.getRotation());
        finder.videoView.play();
    }

    /**
     * 避免 Activity 重建
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClickBack() {
        finish();
    }

    @Override
    public void onClickRotation() {
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onClickNext() {
        if (index == videoList.size()) return;
        index += 1;
        initVideo();
    }

    private static void playVideo(Activity activity, ArrayList<Video> videoList, int index, boolean isLive) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putParcelableArrayListExtra(FIELD_VIDEO, videoList);
        intent.putExtra(FIELD_INDEX, index);
        intent.putExtra(FIELD_LIVE, isLive);
        activity.startActivity(intent);
    }

    public static void playLocalVideo(Activity activity, ArrayList<Video> videoList, int index) {
        playVideo(activity, videoList, index, false);
    }

    public static void playOnlineVideo(Activity activity, String url, String title) {
        VideoUtil.VideoMediaMetadata metadata = VideoUtil.getVideoMediaMetadata(url);
        int rotation = metadata.getRotation();
        int width = metadata.getWidth();
        int height = metadata.getHeight();
        Video video = new Video(0, title, "", url, 0, 0, 0, rotation, width, height);
        ArrayList<Video> videoList = new ArrayList<>();
        videoList.add(video);
        playVideo(activity, videoList, 0, false);
    }

    public static void playLiveVideo(Activity activity, String url, String title) {
        Video video = new Video(0, title, "", url, 0, 0, 0, 0, 1, 0);
        ArrayList<Video> videoList = new ArrayList<>();
        videoList.add(video);
        playVideo(activity, videoList, 0, true);
    }

}
