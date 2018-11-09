package com.johan.mplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.johan.mplayer.R;

/**
 * Created by johan on 2018/11/8.
 */

public class RoundImageView extends ImageView {

    private int width;
    private int height;

    private int roundLeftTop;
    private int roundLeftBottom;
    private int roundRightTop;
    private int roundRightBottom;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        int round = array.getDimensionPixelOffset(R.styleable.RoundImageView_round, 0);
        if (round != 0) {
            roundLeftTop = round;
            roundLeftBottom = round;
            roundRightTop = round;
            roundRightBottom = round;
        } else {
            roundLeftTop = array.getDimensionPixelOffset(R.styleable.RoundImageView_round_left_top, 0);
            roundLeftBottom = array.getDimensionPixelOffset(R.styleable.RoundImageView_round_left_bottom, 0);
            roundRightTop = array.getDimensionPixelOffset(R.styleable.RoundImageView_round_right_top, 0);
            roundRightBottom = array.getDimensionPixelOffset(R.styleable.RoundImageView_round_right_bottom, 0);
        }
        array.recycle();
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(roundLeftTop, 0);
        path.lineTo(width - roundRightTop, 0);
        path.quadTo(width, 0, width, roundRightTop);
        path.lineTo(width, height - roundRightBottom);
        path.quadTo(width, height, width - roundRightBottom, height);
        path.lineTo(roundLeftBottom, height);
        path.quadTo(0, height, 0, height - roundLeftBottom);
        path.lineTo(0, roundLeftTop);
        path.quadTo(0, 0, roundLeftTop, 0);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

}
