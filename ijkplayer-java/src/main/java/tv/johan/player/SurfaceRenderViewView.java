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

    private int videoWidth;
    private int videoHeight;
    private RenderCallback renderCallback;

    public SurfaceRenderViewView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public SurfaceRenderViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (videoWidth == 0 || videoHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
            width = widthSpecSize;
            height = heightSpecSize;
        } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = width * videoHeight / videoWidth;
            if (height > heightSpecSize) {
                height = heightSpecSize;
            }
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.EXACTLY) {
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
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        getHolder().setFixedSize(videoWidth, videoHeight);
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
