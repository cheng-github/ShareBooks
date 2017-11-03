package usst.edu.cn.sharebooks;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import org.litepal.LitePal;


public class ShareApplication extends Application {
    private static String sCacheDir;
    private static Context sAppContext;

    @Override
    public void onCreate(){
        super.onCreate();
        sAppContext = getApplicationContext();//这个就是这个Application的单例的引用，不过它使用了多态这种方法表示而已
        LitePal.initialize(this);//初始化数据库
        Stetho.initializeWithDefaults(this); //使用 facebook网络调试框架
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
