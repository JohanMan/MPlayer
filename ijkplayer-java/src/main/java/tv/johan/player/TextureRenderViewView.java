package tv.johan.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

/**
 * Created by johan on 2018/11/8.
 */

public class TextureRenderViewView extends TextureView implements IRenderView, TextureView.SurfaceTextureListener {

    private Surface surface;
    private RenderMeasureHelper measureHelper;
    private RenderCallback renderCallback;

    public TextureRenderViewView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
        measureHelper = new RenderMeasureHelper();
    }

    public TextureRenderViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
        measureHelper = new RenderMeasureHelper();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureHelper.getWidthMeasureSpec(), measureHelper.getHeightMeasureSpec());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (surfaceTexture == null) return;
        surface = new Surface(surfaceTexture);
        if (renderCallback != null) {
            renderCallback.onRenderCreated(surface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (surfaceTexture == null) return;
        if (surface == null) return;
        if (renderCallback != null) {
            renderCallback.onRenderChanged(surface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) return true;
        if (surface == null) return true;
        if (renderCallback != null) {
            renderCallback.onRenderDestroyed(surface);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

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
        measureHelper.setVideoSize(videoWidth, videoHeight);
        requestLayout();
    }

    /**
     * 设置视频的角度
     * @param videoRotation
     */
    @Override
    public void setVideoRotation(int videoRotation) {
        setRotation(videoRotation);
        measureHelper.setVideoRotation(videoRotation);
        requestLayout();
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
