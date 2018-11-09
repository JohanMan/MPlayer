package tv.johan.player;

import android.view.View;

/**
 * Created by johan on 2018/11/9.
 */

public interface IRenderView {

    void setRenderCallback(RenderCallback callback);

    void setVideoSize(int width, int height);

    View getView();

}
