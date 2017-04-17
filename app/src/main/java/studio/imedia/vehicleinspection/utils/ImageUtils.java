package studio.imedia.vehicleinspection.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by eric on 15/11/22.
 */
public class ImageUtils {

    private static ImageUtils mInstance = null;

    private static final int TYPE_JPEG = 0;
    private static final int TYPE_PNG = 1;

    private ImageUtils() {}

    public static ImageUtils getInstance() {
        if (mInstance == null) {
            synchronized (ImageUtils.class) {
                if (mInstance == null) {
                    mInstance = new ImageUtils();
                }
            }
        }
        return mInstance;
    }

    // TODO 继续往下写。。。
    public String bitmap2StrByBase64(Bitmap bit, int type) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String string = null;
        switch (type) {
            case TYPE_JPEG:
                bit.compress(Bitmap.CompressFormat.JPEG, 100, bos); // 不压缩
                break;
            case TYPE_PNG:
                bit.compress(Bitmap.CompressFormat.PNG, 100, bos);
                break;
        }
        byte[] bytes = bos.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return null;
    }
}
