package tv.johan.player;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * Created by johan on 2018/11/8.
 */

public class OrientationDetector extends OrientationEventListener {

    public static final int ORIENTATION_0 = 0;
    public static final int ORIENTATION_90 = 90;
    public static final int ORIENTATION_180 = 180;
    public static final int ORIENTATION_270 = 270;

    private int lastOrientation = ORIENTATION_0;
    private OnScreenOrientationChangedListener onScreenOrientationChangedListener;

    public OrientationDetector(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN)  return;
        if (orientation > 350 || orientation < 10) {
            orientation = ORIENTATION_0;
        } else if (orientation > 80 && orientation < 100) {
            orientation = ORIENTATION_90;
        } else if (orientation > 170 && orientation < 190) {
            orientation = ORIENTATION_180;
        } else if (orientation > 260 && orientation < 280) {
            orientation = ORIENTATION_270;
        } else {
            orientation = lastOrientation;
        }
        if (lastOrientation == orientation) return;
        if (onScreenOrientationChangedListener != null) {
            onScreenOrientationChangedListener.onScreenOrientationChanged(orientation);
        }
        lastOrientation = orientation;
    }

    public void setOnScreenOrientationChangedListener(OnScreenOrientationChangedListener onScreenOrientationChangedListener) {
        this.onScreenOrientationChangedListener = onScreenOrientationChangedListener;
    }

    public interface OnScreenOrientationChangedListener {
        void onScreenOrientationChanged(int orientation);
    }

}
