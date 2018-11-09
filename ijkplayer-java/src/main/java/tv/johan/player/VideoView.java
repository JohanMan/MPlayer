package tv.johan.player;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * Created by johan on 2018/11/8.
 * Can not keep screen on when play because {@link Surface} is used instead of {@link android.view.SurfaceHolder}
 * See {@link IjkMediaPlayer#setDisplay(SurfaceHolder)}
 * See {@link IjkMediaPlayer#setSurface(Surface)}
 */

public class VideoView extends FrameLayout implements MediaController.MediaPlayerControl, RenderCallback {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_PREPARING = 2;
    private static final int STATE_PREPARED = 3;
    private static final int STATE_STARTED = 4;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_PLAYBACK_COMPLETED = 6;

    private static final int ERROR_UNABLE_OPEN = 666;
    private static final String ERROR_UNABLE_OPEN_DESCRIBE = "Unable to open content : %s";

    private Context appContext;
    private IMediaPlayer mediaPlayer;
    private IRenderView renderView;
    private Surface surface;
    private TextView subtitleView;

    private String videoPath;
    private int currentState = STATE_IDLE;
    private long seekPosition;
    private int bufferPercent;

    public VideoView(Context context) {
        super(context);
        init();
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        appContext = getContext().getApplicationContext();
        setBackgroundColor(Color.BLACK);
        initRenderView();
        initSubtitleView();
    }

    private void initRenderView() {
        renderView = new SurfaceRenderViewView(getContext());
        renderView.setRenderCallback(this);
        FrameLayout.LayoutParams renderLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        renderLayoutParams.gravity = Gravity.CENTER;
        addView(renderView.getView(), renderLayoutParams);
    }

    private void initSubtitleView() {
        subtitleView = new TextView(getContext());
        subtitleView.setTextSize(24);
        subtitleView.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams subtitleLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        subtitleLayoutParams.gravity = Gravity.BOTTOM;
        subtitleLayoutParams.bottomMargin = 24;
        addView(subtitleView, subtitleLayoutParams);
    }

    private void initMediaPlayer() {
        if (videoPath == null) return;
        releaseMediaPlayer();
        mediaPlayer = new IjkMediaPlayer();
        mediaPlayer.setSurface(surface);
        try {
            AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.setOnPreparedListener(preparedListener);
            mediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
            mediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
            mediaPlayer.setOnTimedTextListener(timedTextListener);
            mediaPlayer.setOnCompletionListener(completionListener);
            mediaPlayer.setOnErrorListener(errorListener);
            mediaPlayer.setDataSource(videoPath);
            currentState = STATE_INITIALIZED;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder
            // mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepareAsync();
            currentState = STATE_PREPARING;
            bufferPercent = 0;
        } catch (IOException exception) {
            exception.printStackTrace();
            currentState = STATE_ERROR;
            errorListener.onError(mediaPlayer, ERROR_UNABLE_OPEN, 0);
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    /**
     * 播放器准备监听器
     * 调用 {@link IMediaPlayer#prepareAsync()} 之后会被调用
     * 准备完成之前不能调用 {@link IMediaPlayer#start()}
     */
    private IMediaPlayer.OnPreparedListener preparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer player) {
            currentState = STATE_PREPARED;
            if (seekPosition != 0) {
                player.seekTo(seekPosition);
            }
            player.start();
        }
    };

    /**
     * 播放器视频大小改变监听器
     * 这里可以获取视频大小 用于更新 Render View 的大小
     */
    private IMediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer player, int width, int height, int sar_num, int sar_den) {
            int videoWidth = player.getVideoWidth();
            int videoHeight = player.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                renderView.setVideoSize(videoWidth, videoHeight);
            }
        }
    };

    /**
     * 播放器缓存监听器
     */
    private IMediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer player, int percent) {
            bufferPercent = percent;
        }
    };

    /**
     * 播放器进度条监听器
     * 注意关键帧
     */
    private IMediaPlayer.OnSeekCompleteListener seekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer player) {

        }
    };

    /**
     * 媒体文字时间监听器
     * 可用于实现外挂字幕功能
     */
    private IMediaPlayer.OnTimedTextListener timedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            if (text != null) {
                subtitleView.setText(text.getText());
            }
        }
    };

    /**
     * 播放完成监听器
     */
    private IMediaPlayer.OnCompletionListener completionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer player) {
            currentState = STATE_PLAYBACK_COMPLETED;
        }
    };

    /**
     * 播放器出错监听器
     */
    private IMediaPlayer.OnErrorListener errorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer player, int what, int extra) {
            currentState = STATE_ERROR;
            return false;
        }
    };

    /**
     * ==============================  RenderCallback  ==============================
     */

    @Override
    public void onRenderCreated(Surface surface) {
        if (surface == null) return;
        this.surface = surface;
        if (mediaPlayer == null) {
            initMediaPlayer();
        } else {
            mediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void onRenderChanged(Surface surface, int width, int height) {

    }

    @Override
    public void onRenderDestroyed(Surface surface) {
        seekPosition = mediaPlayer.getCurrentPosition();
        this.surface = null;
        releaseMediaPlayer();
    }

    /**
     * ==============================  MediaController.MediaPlayerControl  ==============================
     */

    @Override
    public void start() {
        // Prepared can start
        // Paused can continue start
        // Completed can restart
        if (mediaPlayer != null && (
                currentState == STATE_PREPARED
                        || currentState == STATE_PAUSED
                        || currentState == STATE_PLAYBACK_COMPLETED)) {
            mediaPlayer.start();
        }
        currentState = STATE_STARTED;
    }

    @Override
    public void pause() {
        // Only Started can pause
        if (mediaPlayer != null && currentState == STATE_STARTED) {
            mediaPlayer.pause();
        }
        currentState = STATE_PAUSED;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        // Prepared can seek to
        // Started can seek to
        // Paused can seek to
        // Completed can seek to
        if (mediaPlayer != null && (
                currentState == STATE_PREPARED
                        || currentState == STATE_STARTED
                        || currentState == STATE_PAUSED
                        || currentState == STATE_PLAYBACK_COMPLETED
                )) {
            mediaPlayer.seekTo(position);
        } else {
            // Record Seek Position
            seekPosition = position;
        }
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mediaPlayer != null) {
            return bufferPercent;
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    /**
     * 是否处于播放器正常状态
     * @return
     */
    private boolean isInPlaybackState() {
        return (mediaPlayer != null &&
                currentState != STATE_ERROR &&
                currentState != STATE_IDLE &&
                currentState != STATE_PREPARING);
    }

    /**
     * 设置视频文件路径
     * @param videoPath
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    /**
     * 设置视频的角度
     * @param rotation
     */
    public void setVideoRotation(int rotation) {
        this.renderView.getView().setRotation(rotation);
    }

    /**
     * 开始播放
     */
    public void play() {
        initMediaPlayer();
    }

}
