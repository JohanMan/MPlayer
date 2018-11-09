package com.johan.async.task;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by johan on 2018/11/7.
 */

public class TaskManager implements Application.ActivityLifecycleCallbacks {

    private static class TaskManagerHolder {
        private static final TaskManager INSTANCE = new TaskManager();
    }

    private Stack<String> tagStack;
    private Map<String, List<Task>> taskMap;

    public TaskManager() {
        tagStack = new Stack<>();
        taskMap = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        String tag = activity.getClass().getName();
        tagStack.push(tag);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        String tag = activity.getClass().getName();
        clear(tag);
        tagStack.pop();
    }

    private void add(Task task) {
        if (tagStack.size() == 0) return;
        String tag = tagStack.peek();
        if (task.getTag() != null) {
            tag = task.getTag();
        }
        if (tag == null) return;
        List<Task> taskList = taskMap.get(tag);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(task);
        taskMap.put(tag, taskList);
    }

    private void remove(Task task) {
        if (tagStack.size() == 0) return;
        String tag = tagStack.peek();
        if (task.getTag() != null) {
            tag = task.getTag();
        }
        if (tag == null) return;
        List<Task> taskList = taskMap.get(tag);
        if (taskList == null) return;
        taskList.remove(task);
    }

    private void clear(String tag) {
        List<Task> taskList = taskMap.get(tag);
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                task.cancel(true, false);
            }
            taskList.clear();
        }
        taskMap.remove(tag);
    }

    /**
     * 初始化
     * @param application
     */
    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(TaskManagerHolder.INSTANCE);
    }

    /**
     * 管理任务
     * @param task
     */
    public static void manager(Task task) {
        TaskManagerHolder.INSTANCE.add(task);
    }

    /**
     * 解除管理任务
     * @param task
     */
    public static void release(Task task) {
        TaskManagerHolder.INSTANCE.remove(task);
    }

}
