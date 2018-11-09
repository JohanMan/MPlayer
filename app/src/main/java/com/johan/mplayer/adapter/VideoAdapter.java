package com.johan.mplayer.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.johan.data.module.Video;
import com.johan.mplayer.R;
import com.johan.mplayer.helper.BitmapHelper;
import com.johan.mplayer.view.RoundImageView;
import com.johan.utils.FileUtil;
import com.johan.utils.TimeUtil;

import java.util.List;

/**
 * Created by johan on 2018/11/7.
 */

public class VideoAdapter extends BaseAdapter <Video> {

    public VideoAdapter(List<Video> dataList) {
        super(dataList);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_video;
    }

    @Override
    public void bindView(ViewHolder holder, int position, Video data, int viewType) {
        RoundImageView videoImageView = holder.getView(R.id.video_image_view);
        videoImageView.setTag(data.getPath());
        BitmapHelper.showVideoImage(data.getPath(), R.drawable.img_video_holder, videoImageView);
        TextView videoNameView = holder.getView(R.id.video_name_view);
        videoNameView.setText(data.getTitle());
        TextView videoTipView = holder.getView(R.id.video_tip_view);
        videoTipView.setText(TimeUtil.formatDuration(data.getDuration()) + " | " +
                FileUtil.formatFileSize(data.getSize()) + " | " +
                TimeUtil.formatTime(data.getTime(), "yyyy.MM.dd"));
    }

}