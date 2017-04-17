package studio.imedia.vehicleinspection.utils;


import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import static android.graphics.BitmapFactory.Options;

/**
 * Created by eric on 15/6/14.
 */
public class PicUtils {

    public static final Bitmap getBitmap(String fileName) {
        Bitmap bitmap = null;
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);
            options.inSampleSize = (int) Math.max(1, Math.ceil(Math.max(
                    options.outHeight / 1024f,
                    options.outHeight / 1024f)));
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(fileName, options);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return bitmap;
    }

    public static String getDataColumn(Context context, Uri uri,
                                String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (null != cursor && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitkat && DocumentsContract.isDocumentUri(context, uri)) { // DocumentProvider
            if (isExternalStorageDocument(uri)) { // ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = MediaStore.MediaColumns._ID + "=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
            if (isGooglePhtotsUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhtotsUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 判断本地是否存有头像的图片
     * 若有，将图片加载到imageview，并返回true
     * 若没有，则返回false
     *
     * @param filePath
     * @return
     */
    public static boolean isAvatarExisted(String filePath, ImageView img) {
        File file = new File(filePath);
        Bitmap bitmap;
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(filePath);
            img.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    /**
     * 让图片宽高比例适配控件的宽高比例
     * @param bmp
     * @param view
     * @return
     */
    public static Bitmap fitView(Bitmap bmp, View view) {
        Bitmap bitmap;
        int outputWidth = view.getWidth();
        int outputHeight = view.getHeight();

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        int cutWidth = bmpWidth;
        int cutHeight = bmpHeight;

        double scaleWidth = bmpWidth * 1.0 / outputWidth;
        double scaleHeight = bmpHeight * 1.0 / outputHeight;

        int offsetX = 0;
        int offsetY = 0;

        if (scaleWidth > scaleHeight) {
            cutWidth = (int) (scaleHeight * outputWidth);
            offsetX = (bmpWidth - cutWidth) / 2;
        } else {
            cutHeight = (int) (scaleWidth * outputHeight);
            offsetY = (bmpHeight - cutHeight) / 2;
        }

        bitmap = Bitmap.createBitmap(bmp, offsetX, offsetY, cutWidth, cutHeight);

        return bitmap;

    }
}
