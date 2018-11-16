package tv.johan.player;

/**
 * Created by johan on 2018/11/12.
 */

public class VideoHelper {

    /**
     * 是否是宽屏视频
     * 视频的宽大于高
     * @param videoWidth
     * @param videoHeight
     * @param videoRotation
     * @return
     */
    public static boolean isWidescreenVideo(int videoWidth, int videoHeight, int videoRotation) {
        if (videoRotation == 90 || videoRotation == 270) {
            int temp = videoWidth;
            videoWidth = videoHeight;
            videoHeight = temp;
        }
        return videoWidth > videoHeight;
    }

}
