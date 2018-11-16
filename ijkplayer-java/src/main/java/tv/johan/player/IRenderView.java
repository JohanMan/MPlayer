package tv.johan.player;

import android.view.View;

/**
 * Created by johan on 2018/11/9.
 */

public interface IRenderView {

    // 适屏 (不会剪切)
    int AR_FIT_PARENT = 1;
    // 宽屏 (宽占满 可能剪切)
    int AR_WIDTH_PARENT = 2;
    // 高屏 (高占满 可能剪切)
    int AR_HEIGHT_PARENT = 3;

    void setRenderCallback(RenderCallback callback);

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoRotation(int videoRotation);

    void setAspectRatio(int aspectRatio);

    View getView();

}
