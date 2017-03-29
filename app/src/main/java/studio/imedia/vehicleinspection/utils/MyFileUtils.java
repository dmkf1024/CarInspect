package studio.imedia.vehicleinspection.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by eric on 15/10/25.
 */
public class MyFileUtils {


    public static void deleteAllFiles(String folderPath, String suffix) {
        File file = new File(folderPath);
        File[] fileList = file.listFiles();
        if (null != fileList) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].toString().endsWith("." + suffix) ||
                        fileList[i].toString().endsWith("." + suffix.toUpperCase())) {
                    fileList[i].delete();
                }
            }
        }
    }
}
