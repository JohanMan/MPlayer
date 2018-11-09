package com.johan.mplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.johan.mplayer.R;
import com.johan.view.finder.ViewFinder;
import com.johan.view.finder.ViewFinderFactory;

import java.lang.reflect.Method;

/**
 * Created by johan on 2018/11/6.
 */

public abstract class BaseActivity <F extends ViewFinder> extends AppCompatActivity {

    protected F finder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        finder = ViewFinderFactory.create(this);
        finder.find(this);
    }

    /**
     * 指定 Activity 布局文件
     * @return
     */
    protected abstract int getActivityLayout();

    /**
     *  初始化 Toolbar
     */
    protected void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * 初始化 Toolbar 带返回
     */
    protected void initToolbarWidthBack() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.img_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 显示 Menu 图标
     * @param view
     * @param menu
     * @return
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu == null) return super.onPrepareOptionsPanel(view, menu);
        if (!menu.getClass().getSimpleName().equals("MenuBuilder")) return super.onPrepareOptionsPanel(view, menu);
        try{
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

}
