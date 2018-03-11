package usst.edu.cn.sharebooks.util;


import android.widget.Toast;

import usst.edu.cn.sharebooks.ShareApplication;

public class ToastUtil {
    public static void showShort(String msg) {
            Toast.makeText(ShareApplication.getAppContext(), msg, Toast.LENGTH_SHORT).show();
            }
            //将吐司信息直接通过这个BaseApplication.getAppContext() 显示在任意activity
            public static void showLong(String msg) {
            Toast.makeText(ShareApplication.getAppContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
