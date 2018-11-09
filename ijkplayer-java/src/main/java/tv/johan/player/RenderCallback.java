package tv.johan.player;

import android.view.Surface;

/**
 * Created by johan on 2018/11/9.
 */

public interface RenderCallback {

    void onRenderCreated(Surface surface);

    void onRenderChanged(Surface surface, int width, int height);

    void onRenderDestroyed(Surface surface);

}
