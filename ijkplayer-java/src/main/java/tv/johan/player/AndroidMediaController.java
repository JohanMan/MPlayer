package tv.johan.player;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import tv.danmaku.ijk.media.player.R;

/**
 * Created by johan on 2018/11/12.
 */

public class AndroidMediaController implements IMediaController, View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, SettingsContentObserver.OnVolumeChangeListener {

    private static final int ANIMATION_TIME = 500;
    private static final int TOUCH_STEP = 30;
    private static final int PROGRESS_STEP_LENGTH = 1000;
    private static final int BRIGHT_STEP_LENGTH = 4;
    private static final float VOLUME_STEP_LENGTH = 1f;
    private static final int SHOWING_TIME_OUT = 6000;

    private static final int INTENT_CLICK = 1;
    private static final int INTENT_PROGRESS = 2;
    private static final int INTENT_BRIGHT = 3;
    private static final int INTENT_VOLUME = 4;

    private static final float MAX_SPEED = 3;
    private static final float MIN_SPEED = 0.6f;
    private static final float SPEED_STEP = 0.2f;

    private Context context;
    private View controllerView;
    private LinearLayout topLayout;
    private LinearLayout bottomLayout;
    private ImageView backView;
    private TextView nameView;
    private ImageView rotationView;
    private ImageView playView;
    private ImageView playNextView;
    private TextView timeView;
    private TextView speedDecreaseView;
    private TextView speedView;
    private TextView speedIncreaseView;
    private SeekBar seekBar;
    private LinearLayout tipLayout;
    private ImageView tipIconView;
    private TextView tipView;

    private OnClickBackListener onClickBackListener;
    private OnClickRotationListener onClickRotationListener;
    private OnClickNextListener onClickNextListener;

    private IVideoPlayer player;
    private Handler updateHandler;
    private SettingsContentObserver observer;
    private boolean isShowing;
    private float startX, startY;
    private int startPosition;
    private int startBright;
    private int startVolume;
    private int touchIntent;
    private long lastClickTime;
    private int bright;
    private int volume;
    private float speed = 1.0f;

    public AndroidMediaController(Context context) {
        this.context = context;
        updateHandler = new Handler(Looper.getMainLooper());
        initView();
        bright = SystemHelper.getScreenBright((Activity) context);
        volume = SystemHelper.getMusicVolume(context);
    }

    private void initView() {
        controllerView = LayoutInflater.from(context).inflate(R.layout.view_media_controller, null);
        controllerView.setOnTouchListener(this);
        topLayout = (LinearLayout) controllerView.findViewById(R.id.media_controller_top);
        bottomLayout = (LinearLayout) controllerView.findViewById(R.id.media_controller_bottom);
        backView = (ImageView) controllerView.findViewById(R.id.media_controller_back);
        backView.setOnClickListener(this);
        nameView = (TextView) controllerView.findViewById(R.id.media_controller_name);
        rotationView = (ImageView) controllerView.findViewById(R.id.media_controller_rotation);
        rotationView.setOnClickListener(this);
        playView = (ImageView) controllerView.findViewById(R.id.media_controller_play);
        playView.setOnClickListener(this);
        playNextView = (ImageView) controllerView.findViewById(R.id.media_controller_play_next);
        playNextView.setOnClickListener(this);
        timeView = (TextView) controllerView.findViewById(R.id.media_controller_time);
        speedDecreaseView = (TextView) controllerView.findViewById(R.id.media_controller_speed_decrease);
        speedDecreaseView.setOnClickListener(this);
        speedView = (TextView) controllerView.findViewById(R.id.media_controller_speed);
        speedIncreaseView = (TextView) controllerView.findViewById(R.id.media_controller_speed_increase);
        speedIncreaseView.setOnClickListener(this);
        seekBar = (SeekBar) controllerView.findViewById(R.id.media_controller_seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        tipLayout = (LinearLayout) controllerView.findViewById(R.id.media_controller_tip_layout);
        tipIconView = (ImageView) controllerView.findViewById(R.id.media_controller_tip_icon);
        tipView = (TextView) controllerView.findViewById(R.id.media_controller_tip);
        setEnabled(false);
        isShowing = true;
        startTimer();
        registerVolumeChangeReceiver();
    }

    private void startTimer() {
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player.isPlaying()) {
                    seekBar.setMax(player.getDuration());
                    seekBar.setProgress(player.getCurrentPosition());
                    seekBar.setSecondaryProgress(player.getBufferPercentage() * player.getDuration());
                    String time = TimeUtil.formatDuration(player.getCurrentPosition()) + "/" + TimeUtil.formatDuration(player.getDuration());
                    timeView.setText(time);
                }
                startTimer();
            }
        }, 500);
    }

    private Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void registerVolumeChangeReceiver() {
        observer = new SettingsContentObserver(context, updateHandler);
        observer.setOnVolumeChangeListener(this);
        context.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, observer);
    }

    private void unregisterVolumeChangeReceiver() {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public void setEnabled(boolean enabled) {
        controllerView.setEnabled(enabled);
        if (enabled) {
            playView.setImageResource(R.drawable.img_play_stop);
            show(SHOWING_TIME_OUT);
        } else {
            playView.setImageResource(R.drawable.img_play_start);
        }
    }

    @Override
    public View getMediaControllerView() {
        return controllerView;
    }

    @Override
    public void setVideoPlayer(IVideoPlayer player) {
        this.player = player;
        speedDecreaseView.setVisibility(player.canSetSpeed() ? View.VISIBLE : View.GONE);
        speedView.setVisibility(player.canSetSpeed() ? View.VISIBLE : View.GONE);
        speedIncreaseView.setVisibility(player.canSetSpeed() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void show(int timeout) {
        show();
        updateHandler.removeCallbacks(hideAction);
        updateHandler.postDelayed(hideAction, timeout);
    }

    @Override
    public void show() {
        if (isShowing) return;
        isShowing = true;
        updateHandler.removeCallbacks(hideAction);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator topAnimator = ObjectAnimator.ofFloat(topLayout, "translationY", -topLayout.getHeight(), 0);
        ObjectAnimator bottomAnimator = ObjectAnimator.ofFloat(bottomLayout, "translationY", bottomLayout.getHeight(), 0);
        ObjectAnimator seekBarAnimator = ObjectAnimator.ofFloat(seekBar, "alpha", 0, 1);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(rotationView, "alpha", 0, 1);
        animatorSet.play(topAnimator).with(bottomAnimator).with(seekBarAnimator).with(rotationAnimator);
        animatorSet.setDuration(ANIMATION_TIME);
        animatorSet.start();
    }

    @Override
    public void hide() {
        if (!isShowing) return;
        isShowing = false;
        updateHandler.removeCallbacks(hideAction);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator topAnimator = ObjectAnimator.ofFloat(topLayout, "translationY", 0, -topLayout.getHeight());
        ObjectAnimator bottomAnimator = ObjectAnimator.ofFloat(bottomLayout, "translationY", 0, bottomLayout.getHeight());
        ObjectAnimator seekBarAnimator = ObjectAnimator.ofFloat(seekBar, "alpha", 1, 0);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(rotationView, "alpha", 1, 0);
        animatorSet.play(topAnimator).with(bottomAnimator).with(seekBarAnimator).with(rotationAnimator);
        animatorSet.setDuration(ANIMATION_TIME);
        animatorSet.start();
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.media_controller_back) {
            if (onClickBackListener != null) {
                onClickBackListener.onClickBack();
            }
        } else if (id == R.id.media_controller_rotation) {
            if (onClickRotationListener != null) {
                onClickRotationListener.onClickRotation();
            }
        } else if (id == R.id.media_controller_play) {
            if (player.isPlaying()) {
                player.pause();
                playView.setImageResource(R.drawable.img_play_start);
            } else {
                player.start();
                playView.setImageResource(R.drawable.img_play_stop);
            }
        } else if (id == R.id.media_controller_play_next) {
            if (onClickNextListener != null) {
                onClickNextListener.onClickNext();
            }
        } else if (id == R.id.media_controller_play_next) {
            if (onClickNextListener != null) {
                onClickNextListener.onClickNext();
            }
        } else if (id == R.id.media_controller_speed_decrease) {
            if (speed <= MIN_SPEED) return;
            speed -= SPEED_STEP;
            player.setSpeed(speed);
            speedView.setText(String.format("%.1f 倍速", speed));
            show(SHOWING_TIME_OUT);
        } else if (id == R.id.media_controller_speed_increase) {
            if (speed >= MAX_SPEED) return;
            speed += SPEED_STEP;
            player.setSpeed(speed);
            speedView.setText(String.format("%.1f 倍速", speed));
            show(SHOWING_TIME_OUT);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                startX = event.getX();
                startY = event.getY();
                touchIntent = INTENT_CLICK;
                startPosition = player.getCurrentPosition();
                startBright = bright;
                startVolume = volume;
                break;
            case MotionEvent.ACTION_MOVE :
                float moveX = event.getX();
                float moveY = event.getY();
                if (Math.abs(startX - moveX) < TOUCH_STEP && Math.abs(startY - moveY) < TOUCH_STEP) break;
                if (touchIntent == INTENT_CLICK) {
                    if (Math.abs(startX - moveX) > Math.abs(startY - moveY)) {
                        // 进度
                        touchIntent = INTENT_PROGRESS;
                    } else {
                        if (startX < controllerView.getWidth() / 2) {
                            // 亮度
                            touchIntent = INTENT_BRIGHT;
                        } else {
                            // 音量
                            touchIntent = INTENT_VOLUME;
                        }
                    }
                }
                tipLayout.setVisibility(View.VISIBLE);
                // 处理进度
                if (touchIntent == INTENT_PROGRESS) {
                    int move = (int) (moveX - startX);
                    move /= TOUCH_STEP;
                    move *= PROGRESS_STEP_LENGTH;
                    int position = startPosition + move;
                    player.seekTo(position);
                    String tip = TimeUtil.formatDuration(Math.abs(move));
                    tip = move > 0 ? "+ " + tip : "- " + tip;
                    tipView.setText(tip);
                    tipIconView.setVisibility(View.GONE);
                } else if (touchIntent == INTENT_VOLUME) {
                    int move = (int) (startY - moveY);
                    move /= TOUCH_STEP;
                    move *= VOLUME_STEP_LENGTH;
                    volume = startVolume + move;
                    if (volume < SystemHelper.MIN_VOLUME) {
                        volume = SystemHelper.MIN_VOLUME;
                    }
                    if (volume > SystemHelper.MAX_VOLUME) {
                        volume = SystemHelper.MAX_VOLUME;
                    }
                    SystemHelper.setMusicVolume(context, volume);
                    tipView.setText(String.valueOf(volume));
                    tipIconView.setImageResource(R.drawable.icon_volume);
                    tipIconView.setVisibility(View.VISIBLE);
                } else if (touchIntent == INTENT_BRIGHT) {
                    int move = (int) (startY - moveY);
                    move /= TOUCH_STEP;
                    move *= BRIGHT_STEP_LENGTH;
                    bright = startBright + move;
                    if (bright < SystemHelper.MIN_BRIGHT) {
                        bright = SystemHelper.MIN_BRIGHT;
                    }
                    if (bright > SystemHelper.MAX_BRIGHT) {
                        bright = SystemHelper.MAX_BRIGHT;
                    }
                    SystemHelper.setScreenBright((Activity) context, bright);
                    tipView.setText(bright * 100 / SystemHelper.MAX_BRIGHT + "%");
                    tipIconView.setImageResource(R.drawable.icon_bright);
                    tipIconView.setVisibility(View.VISIBLE);
                }
                break;
            case MotionEvent.ACTION_UP :
                if (touchIntent == INTENT_CLICK) {
                    if (System.currentTimeMillis() - lastClickTime < 500) {
                        if (player.isPlaying()) {
                            player.pause();
                            playView.setImageResource(R.drawable.img_play_start);
                        } else {
                            player.start();
                            playView.setImageResource(R.drawable.img_play_stop);
                        }
                        lastClickTime = 0;
                    } else {
                        if (isShowing) {
                            hide();
                        } else {
                            show(SHOWING_TIME_OUT);
                        }
                        lastClickTime = System.currentTimeMillis();
                    }
                }
                tipLayout.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            player.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onVolumeChange(int volume) {
        this.volume = volume;
    }

    public void setVideoName(String videoName) {
        nameView.setText(videoName);
    }

    public void canNext(boolean canNext) {
        playNextView.setVisibility(canNext ? View.VISIBLE : View.GONE);
    }

    public void showProgress(boolean isShow) {
        timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void canSpeed(boolean canSpeed) {
        speedDecreaseView.setVisibility(canSpeed ? View.VISIBLE : View.GONE);
        speedView.setVisibility(canSpeed ? View.VISIBLE : View.GONE);
        speedIncreaseView.setVisibility(canSpeed ? View.VISIBLE : View.GONE);
    }

    public void reset() {
        speed = 1.0f;
        player.setSpeed(speed);
        speedView.setText(String.format("%.1f 倍速", speed));
    }

    public void release() {
        unregisterVolumeChangeReceiver();
    }

    public void setOnClickBackListener(OnClickBackListener onClickBackListener) {
        this.onClickBackListener = onClickBackListener;
    }

    public interface OnClickBackListener {
        void onClickBack();
    }

    public void setOnClickRotationListener(OnClickRotationListener onClickRotationListener) {
        this.onClickRotationListener = onClickRotationListener;
    }

    public interface OnClickRotationListener {
        void onClickRotation();
    }

    public void setOnClickNextListener(OnClickNextListener onClickNextListener) {
        this.onClickNextListener = onClickNextListener;
    }

    public interface OnClickNextListener {
        void onClickNext();
    }

}
