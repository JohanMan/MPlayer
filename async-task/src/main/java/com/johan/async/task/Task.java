package com.johan.async.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johan on 2018/11/7.
 */

public class Task <T> {

    /**
     * Executor
     * see AsyncTask {@link android.os.AsyncTask}
     */

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(128);
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Async Task #" + count.getAndIncrement());
        }
    };

    private static final Executor executor;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, workQueue, threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        executor = threadPoolExecutor;
    }

    /**
     * Main Handler
     */

    private static MainHandler mainHandler;

    private static MainHandler getMainHandler() {
        synchronized (Task.class) {
            if (mainHandler == null) {
                mainHandler = new MainHandler();
            }
            return mainHandler;
        }
    }

    private static class MainHandler extends Handler {

        public MainHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message message) {
            TaskResult result = (TaskResult) message.obj;
            switch (result.action) {
                case ACTION_POST_UI :
                    result.task.doUI(result.data);
                    break;
                case ACTION_POST_CANCEL :
                    result.task.doCancel();
                    break;
                case ACTION_POST_ERROR :
                    result.task.doError(result.throwable);
                    break;
                case ACTION_POST_PROGRESS :
                    result.task.doProgress(result.progress);
                    break;
            }
        }

    }

    /**
     * Task Result
     */

    private static final int ACTION_POST_UI = 0x1;
    private static final int ACTION_POST_CANCEL = 0x2;
    private static final int ACTION_POST_ERROR = 0x3;
    private static final int ACTION_POST_PROGRESS = 0X4;

    private static class TaskResult <T> {

        private int action;
        private Task<T> task;
        private T data;
        private Throwable throwable;
        private Progress progress;

        public TaskResult(int action, Task<T> task, T data, Throwable throwable, Progress progress) {
            this.action = action;
            this.task = task;
            this.data = data;
            this.throwable = throwable;
            this.progress = progress;
        }

        public static <T> TaskResult<T> newUIResult(Task<T> task, T data) {
            return new TaskResult<>(ACTION_POST_UI, task, data, null, null);
        }

        public static <T> TaskResult<T> newCancelResult(Task<T> task) {
            return new TaskResult<>(ACTION_POST_CANCEL, task, null, null, null);
        }

        public static <T> TaskResult<T> newErrorResult(Task<T> task, Throwable throwable) {
            return new TaskResult<>(ACTION_POST_ERROR, task, null, throwable, null);
        }

        public static <T> TaskResult<T> newProgressResult(Task<T> task, Progress progress) {
            return new TaskResult<>(ACTION_POST_ERROR, task, null, null, progress);
        }

    }

    /**
     * Progress
     * see Message {@link Message}
     */

    public static class Progress {

        private static final Object poolSync = new Object();
        private static Progress pool;
        private static int poolSize = 0;
        private static final int MAX_POOL_SIZE = 50;

        public static Progress obtain() {
            synchronized (poolSync) {
                if (pool != null) {
                    Progress progress = pool;
                    pool = progress.next;
                    pool.next = null;
                    poolSize--;
                    return progress;
                }
            }
            return new Progress();
        }

        public int intValue;
        public long longValue;
        public String stringValue;
        public float floatValue;
        public double doubleValue;
        public boolean booleanValue;
        public Object objectValue;
        public Progress next;

        public void recycle() {
            clear();
            synchronized (poolSync) {
                if (poolSize < MAX_POOL_SIZE) {
                    next = pool;
                    pool = this;
                    poolSize++;
                }
            }
        }

        private void clear() {
            intValue = 0;
            longValue = 0l;
            stringValue = null;
            floatValue = 0f;
            doubleValue = 0d;
            booleanValue = false;
            objectValue = null;
        }

    }

    /**
     * Task
     */

    private BackgroundAction<T> backgroundAction;
    private UIAction<T> uiAction;
    private CancelAction cancelAction;
    private ErrorAction errorAction;
    private ProgressAction progressAction;
    private FutureTask<T> futureTask;
    private String tag;

    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final AtomicBoolean isFinished = new AtomicBoolean();

    public Task(BackgroundAction<T> backgroundAction, UIAction<T> uiAction, CancelAction cancelAction, ErrorAction errorAction, ProgressAction progressAction) {
        this.backgroundAction = backgroundAction;
        this.uiAction = uiAction;
        this.cancelAction = cancelAction;
        this.errorAction = errorAction;
        this.progressAction = progressAction;
        Callable<T> callable = createCallable();
        futureTask = new FutureTask<>(callable);
    }

    private Callable<T> createCallable() {
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() throws Exception {
                T data = null;
                try {
                    if (backgroundAction != null) {
                        data = backgroundAction.doBackground(Task.this);
                    }
                    postUI(data);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    postError(exception);
                }
                return data;
            }
        };
        return callable;
    }

    /**
     * Post To Handler
     */

    private void postUI(T data) {
        Message message = getMainHandler().obtainMessage(0, TaskResult.newUIResult(this, data));
        message.sendToTarget();
    }

    private void postCancel() {
        Message message = getMainHandler().obtainMessage(0, TaskResult.newCancelResult(this));
        message.sendToTarget();
    }

    /**
     * throw error
     * attention : progress must use {@link Progress#obtain()} create
     */

    public void postError(Throwable throwable) {
        Message message = getMainHandler().obtainMessage(0, TaskResult.newErrorResult(this, throwable));
        message.sendToTarget();
    }

    /**
     * update progress by user
     * attention : progress must use {@link Progress#obtain()} create
     */

    public void postProgress(Progress progress) {
        Message message = getMainHandler().obtainMessage(0, TaskResult.newProgressResult(this, progress));
        message.sendToTarget();
    }

    /**
     * Do On UI Thread
     * see MainHandler {@link MainHandler}
     */

    private void doUI(T data) {
        if (uiAction != null) {
            uiAction.doUI(data);
        }
        isFinished.set(true);
        TaskManager.release(this);
    }

    private void doCancel() {
        if (cancelAction != null) {
            cancelAction.doCancel();
        }
    }

    private void doError(Throwable throwable) {
        if (errorAction != null) {
            errorAction.doError(throwable);
        }
        isFinished.set(true);
        TaskManager.release(this);
    }

    private void doProgress(Progress progress) {
        if (progressAction != null) {
            progressAction.doProgress(progress);
        }
        progress.recycle();
    }

    /**
     * Tag get set
     */

    private void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    /**
     * execute
     */

    private void execute() {
        executor.execute(futureTask);
        TaskManager.manager(this);
    }

    public void cancel(boolean mayInterruptIfRunning, boolean release) {
        if (isFinished.get()) return;
        if (isCanceled.get()) return;
        isCanceled.set(true);
        isFinished.set(true);
        postCancel();
        futureTask.cancel(mayInterruptIfRunning);
        if (release) {
            TaskManager.release(this);
        }
    }

    /**
     * Interface
     */

    public interface BackgroundAction <T> {
        T doBackground(Task<T> task);
    }

    public interface UIAction <T> {
        void doUI(T data);
    }

    public interface CancelAction {
        void doCancel();
    }

    public interface ErrorAction {
        void doError(Throwable throwable);
    }

    public interface ProgressAction {
        void doProgress(Progress progress);
    }

    /**
     * cancel by user
     */

    public void cancel(boolean mayInterruptIfRunning) {
        cancel(mayInterruptIfRunning, true);
    }

    /**
     * Create Task Builder
     */
    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    /**
     * Builder
     */

    public static class Builder <T> {

        private BackgroundAction<T> backgroundAction;
        private UIAction<T> uiAction;
        private CancelAction cancelAction;
        private ErrorAction errorAction;
        private ProgressAction progressAction;
        private String tag;

        public Builder<T> background(BackgroundAction<T> backgroundAction) {
            this.backgroundAction = backgroundAction;
            return this;
        }

        public Builder<T> ui(UIAction<T> uiAction) {
            this.uiAction = uiAction;
            return this;
        }

        public Builder<T> cancel(CancelAction cancelAction) {
            this.cancelAction = cancelAction;
            return this;
        }

        public Builder<T> error(ErrorAction errorAction) {
            this.errorAction = errorAction;
            return this;
        }

        public Builder<T> progress(ProgressAction progressAction) {
            this.progressAction = progressAction;
            return this;
        }

        public Builder<T> tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Task<T> execute() {
            Task<T> task = new Task<>(backgroundAction, uiAction, cancelAction, errorAction, progressAction);
            task.setTag(tag);
            task.execute();
            return task;
        }

    }

}
