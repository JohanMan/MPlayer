package tv.johan.player;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by johan on 2018/11/13.
 */

public class SettingsContentObserver extends ContentObserver {

    private Context context;
    private OnVolumeChangeListener onVolumeChangeListener;
    private int volume;

    public SettingsContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        this.volume = SystemHelper.getMusicVolume(context);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        int newVolume = SystemHelper.getMusicVolume(context);
        if (volume == newVolume) return;
        volume = newVolume;
        if (onVolumeChangeListener != null) {
            onVolumeChangeListener.onVolumeChange(newVolume);
        }
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        this.onVolumeChangeListener = onVolumeChangeListener;
    }

    public interface OnVolumeChangeListener {
        void onVolumeChange(int volume);
    }

}
