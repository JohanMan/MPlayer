package tv.johan.player;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by johan on 2018/11/8.
 */

public class TimeUtil {

    /**
     * 格式化时长
     * @param time
     * @return
     */
    public static String formatDuration(long time) {
        time = time / 1000;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time >= 3600) {
            hour = time / 3600;
            time = time % 3600;
        }
        if (time >= 60) {
            minute = time / 60;
            time = time % 60;
        }
        second = time;
        StringBuilder builder = new StringBuilder();
        if (hour > 0) {
            builder.append(hour < 10 ? "0" + hour : hour).append(":");
        }
        builder.append(minute < 10 ? "0" + minute : minute).append(":");
        builder.append(second < 10 ? "0" + second : second);
        return builder.toString();
    }

    /**
     * 格式化时间
     * @param time
     * @param pattern
     * @return
     */
    public static String formatTime(long time, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date(time));
    }

}
