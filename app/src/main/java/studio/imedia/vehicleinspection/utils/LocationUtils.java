package studio.imedia.vehicleinspection.utils;

/**
 * Created by eric on 15/10/31.
 */
public class LocationUtils {

    private int[] mIndex = null;

    private int mLength = 0;

    private static volatile LocationUtils mInstance = null;

    private LocationUtils() {
    }

    public static LocationUtils getInstance() {
        if (null == mInstance) {
            synchronized (LocationUtils.class) {
                if (null == mInstance) {
                    mInstance = new LocationUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 返回排序后的新序列在旧序列的下标
     *
     * @param origin 为排列的字符处数组
     * @param ordered 已排列的字符串数组
     * @param length 字符处数组的长度
     */
    public void saveIndex(String[] origin, String[] ordered, int length) {
        mLength = length;
        mIndex = new int[mLength];
        for (int i = 0; i < mLength; i++) {
            for (int j = 0; j < mLength; j++) {
                if (ordered[i].equals(origin[j]))
                    mIndex[i] = j;
            }
        }
    }

    /**
     * 返回小标数组
     *
     * @return
     */
    public int[] getIndex() {
        if (mIndex != null)
            return mIndex;
        return null;
    }

    public boolean isIndexNull() {
        if (mIndex != null)
            return false;
        return true;
    }
}
