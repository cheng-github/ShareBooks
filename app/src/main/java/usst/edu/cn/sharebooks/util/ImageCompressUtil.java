package usst.edu.cn.sharebooks.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompressUtil {

    //图片按比例大小压缩方法（根据路径获取图片并压缩）：
    public static Uri getSmallBitmap(Context context, File file, int reqWidth, int reqHeight) {
        try {
            String filePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//开始读入图片，此时把options.inJustDecodeBounds 设回true了
            BitmapFactory.decodeFile(filePath, options);//此时返回bm为空
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);//设置缩放比例
            options.inJustDecodeBounds = false;//重新读入图片，注意此时把options.inJustDecodeBounds 设回false了
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
       //     File currentImageFile = new File(FileUtil.getFileDir(context), SystemClock.currentThreadTimeMillis()+".jpg");
            //压缩好比例大小后不进行质量压缩
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到bos中
            //压缩好比例大小后再进行质量压缩
            //compressImage(bitmap,filePath);
            return Uri.fromFile(file);
        } catch (Exception e) {
//            Log.d("wzc", "类:" + this.getClass().getName() + " 方法：" + Thread.currentThread()
//                    .getStackTrace()[0].getMethodName() + " 异常 " + e);
           return null;
        }
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        try {
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;  //1表示不缩放
            if (height > reqHeight || width > reqWidth) {
                int heightRatio = Math.round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            return inSampleSize;
        } catch (Exception e) {
//            Log.d("wzc", "类:" + this.getClass().getName() + " 方法：" + Thread.currentThread()
//                    .getStackTrace()[0].getMethodName() + " 异常 " + e);
            return 1;
        }
    }

    // 质量压缩法：
    private static Bitmap compressImage(Bitmap image, String filepath) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
