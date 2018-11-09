package com.johan.utils;

/**
 * Created by johan on 2018/11/8.
 */

public class FileUtil {

    /**
     * 格式化文件大小
     * @param fileSize
     * @return
     */
    public static String formatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return "1K";
        } else if (fileSize < 1024 * 1024) {
            return fileSize / 1024 + "K";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return fileSize / 1024 / 1024 + "M";
        } else {
            float size = fileSize / 1024 / 1024 * 1.0f / 1024;
            return String.format("%.1fG", size);
        }
    }

}
