package tv.johan.player;

import android.view.View;

/**
 * Created by johan on 2018/11/8.
 */

public interface IMediaController {

    void setEnabled(boolean enabled);

    View getMediaControllerView();

    void setVideoPlayer(IVideoPlayer player);

    void show(int timeout);

    void show();

    void hide();

    boolean isShowing();

}
