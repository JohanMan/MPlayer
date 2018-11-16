package tv.johan.player;

import android.widget.MediaController;

/**
 * Created by johan on 2018/11/14.
 */

public interface IVideoPlayer extends MediaController.MediaPlayerControl {
    boolean canSetSpeed();
    void setSpeed(float speed);
}
