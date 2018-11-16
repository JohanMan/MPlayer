package tv.johan.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by johan on 2018/11/8.
 * {@link SurfaceView#setRotation(float)} is ineffective
 */

public class SurfaceRenderViewView extends SurfaceView  implements IRenderView, SurfaceHolder.Callback {

    private RenderMeasureHelper measureHelper;
    private RenderCallback renderCallback;

    public SurfaceRenderViewView(Context context) {
        super(context);
        getHolder().addCallback(this);
        measureHelper = new RenderMeasureHelper();
    }

    public SurfaceRenderViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        measureHelper = new RenderMeasureHelper();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureHelper.getWidthMeasureSpec(), measureHelper.getHeightMeasureSpec());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) return;
        if (renderCallback != null) {
            renderCallback.onRenderCreated(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder == null) return;
        if (renderCallback != null) {
            renderCallback.onRenderChanged(holder.getSurface(), width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (holder == null) return;
        if (renderCallback != null) {
            renderCallback.onRenderDestroyed(holder.getSurface());
        }
    }

    /**
     * 设置渲染回调
     * @param renderCallback
     */
    @Override
    public void setRenderCallback(RenderCallback renderCallback) {
        this.renderCallback = renderCallback;
    }

    /**
     * 设置视频大小
     * @param videoWidth
     * @param videoHeight
     */
    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        getHolder().setFixedSize(videoWidth, videoHeight);
        measureHelper.setVideoSize(videoWidth, videoHeight);
        requestLayout();
    }

    /**
     * 设置视频的角度
     * SurfaceView 设置角度无效
     * @param videoRotation
     */
    @Deprecated
    @Override
    public void setVideoRotation(int videoRotation) {

    }

    /**
     * 设置高宽比
     * @param aspectRatio
     */
    @Override
    public void setAspectRatio(int aspectRatio) {
        measureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    /**
     * 获取 View
     * @return
     */
    @Override
    public View getView() {
        return this;
    }

}
