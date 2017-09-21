package usst.edu.cn.sharebooks;

import android.app.Application;
import android.content.Context;


public class ShareApplication extends Application {
    private static String sCacheDir;
    private static Context sAppContext;

    @Override
    public void onCreate(){
        super.onCreate();
        sAppContext = getApplicationContext();//这个就是这个Application的单例的引用，不过它使用了多态这种方法表示而已
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppCacheDir() {
        return sCacheDir;
    }
}
