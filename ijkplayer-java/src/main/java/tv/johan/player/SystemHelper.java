package tv.johan.player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * Created by johan on 2018/11/13.
 */

public class SystemHelper {

    public static final int MAX_BRIGHT = 255;
    public static final int MIN_BRIGHT = 30;
    public static final int MAX_VOLUME = 15;
    public static final int MIN_VOLUME = 0;

    /**
     * 获取屏幕的亮度
     * @param activity
     * @return
     */
    public static int getScreenBright(Activity activity) {
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException exception) {
            exception.printStackTrace();
        }
        return value;
    }

    /**
     * 设置屏幕的亮度
     * @param activity
     * @param value
     */
    public static void setScreenBright(Activity activity, int value) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = value / 255f;
        activity.getWindow().setAttributes(params);
    }

    /**
     * 获取音乐声道的音量
     * @param context
     * @return
     */
    public static int getMusicVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置音乐声道的音量
     * @param context
     * @param volume
     */
    public static void setMusicVolume(Context context, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume == volume) return;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }

}
