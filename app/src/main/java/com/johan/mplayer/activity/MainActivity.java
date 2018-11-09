package com.johan.mplayer.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.johan.core.CoreAction;
import com.johan.core.VideoCore;
import com.johan.data.module.Video;
import com.johan.mplayer.R;
import com.johan.mplayer.adapter.BaseAdapter;
import com.johan.mplayer.adapter.VideoAdapter;
import com.johan.mplayer.helper.PermissionHelper;
import com.johan.mplayer.view.EmptyItemDecoration;
import com.johan.view.finder.AutoFind;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johan on 2018/11/1.
 */

@AutoFind
public class MainActivity extends BaseActivity <MainViewFinder> implements PermissionHelper.OnPermissionCallback {

    private VideoCore videoCore;
    private List<Video> videoList;
    private VideoAdapter videoAdapter;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initCore();
        initView();
        PermissionHelper.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, this);
    }

    /**
     * 初始化 Core
     */
    private void initCore() {
        videoCore = new VideoCore();
    }

    /**
     * 初始化 View
     */
    private void initView() {
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoList);
        videoAdapter.setOnRecyclerViewItemClickListener(new BaseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VideoActivity.startActivity(MainActivity.this, videoList.get(position));
            }
        });
        finder.listView.setLayoutManager(new LinearLayoutManager(this));
        int dividerHeight = getResources().getDimensionPixelOffset(R.dimen.layout_space);
        finder.listView.addItemDecoration(new EmptyItemDecoration(dividerHeight));
        finder.listView.setAdapter(videoAdapter);
    }

    /**
     * 权限相关
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.handlePermissionResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionAccept(int requestCode, String... permissions) {
        loadData();
    }

    @Override
    public void onPermissionRefuse(int requestCode, String... permission) {

    }

    /**
     * 加载数据 视频
     */
    private void loadData() {
        videoCore.loadAllVideo(getApplicationContext(), loadAllVideoAction);
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 点击菜单
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort :
                break;
            case R.id.action_setting :
                break;
            case R.id.action_delete :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取所有视频监听器
     */
    private CoreAction<List<Video>> loadAllVideoAction = new CoreAction<List<Video>>() {
        @Override
        public void doError(Throwable throwable) {
            finder.emptyLayout.setVisibility(View.VISIBLE);
        }
        @Override
        public void doUI(List<Video> data) {
            finder.emptyLayout.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
            videoList.clear();
            videoList.addAll(data);
            videoAdapter.notifyDataSetChanged();
        }
    };

}
