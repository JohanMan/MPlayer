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
    private int videoWidth;
    private int videoHeight;
    private RenderCallback renderCallback;

    public TextureRenderViewView(Context context) {
        super(context);
    }

    public TextureRenderViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (videoWidth == 0 || videoHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
            height = heightSpecSize;
        } else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = width * videoHeight / videoWidth;
            if (height > heightSpecSize) {
                height = heightSpecSize;
            }
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = height * videoWidth / videoHeight;
            if (width > widthSpecSize) {
                width = widthSpecSize;
            }
        } else {
            width = videoWidth;
            height = videoHeight;
            if (height < heightSpecSize) {
                height = heightSpecSize;
                width = height * videoWidth / videoHeight;
            }
            if (width > widthSpecSize) {
                width = widthSpecSize;
                height = width * videoHeight / videoWidth;
            }
        }
        setMeasuredDimension(width, height);
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
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
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
