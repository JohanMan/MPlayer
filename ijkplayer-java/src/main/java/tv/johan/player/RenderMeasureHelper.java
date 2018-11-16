package tv.johan.player;

import android.view.View;

/**
 * Created by johan on 2018/11/12.
 */

public class RenderMeasureHelper {

    private int videoWidth;
    private int videoHeight;
    private int videoRotation;
    private int aspectRatio = IRenderView.AR_FIT_PARENT;

    private int width;
    private int height;

    public void setVideoSize(int videoWidth, int videoHeight) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    public void setVideoRotation(int videoRotation) {
        this.videoRotation = videoRotation;
    }

    public void setAspectRatio(int aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        width = widthMeasureSpec;
        height = heightMeasureSpec;
        if (videoWidth == 0 || videoHeight == 0) return;
        if (videoRotation == 90 || videoRotation == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
            width = widthSpecSize;
            height = heightSpecSize;
        } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = width * videoHeight / videoWidth;
            if (height > heightSpecSize) {
                height = heightSpecSize;
            }
        } else if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = height * videoWidth / videoHeight;
            if (width > widthSpecSize) {
                width = widthSpecSize;
            }
        } else {
            switch (aspectRatio) {
                case IRenderView.AR_FIT_PARENT :
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                    if (width > widthSpecSize) {
                        width = widthSpecSize;
                        height = width * videoHeight / videoWidth;
                    }
                    break;
                case IRenderView.AR_WIDTH_PARENT :
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                    break;
                case IRenderView.AR_HEIGHT_PARENT :
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                    break;
            }
        }
    }

    public int getWidthMeasureSpec() {
        return width;
    }

    public int getHeightMeasureSpec() {
        return height;
    }

}
