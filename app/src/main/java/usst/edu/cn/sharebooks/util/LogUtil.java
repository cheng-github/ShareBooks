package usst.edu.cn.sharebooks.util;

import android.util.Log;

/**
 *  日志打印工具类
 * Created by Cheng on 2017/7/11.
 */

public class LogUtil {
    private static  String TAG = "SWC";

    /**
     * 日志输出单个字符串
     *
     */
    public static void log(String value){
            Log.i(TAG,value);
    }

}
