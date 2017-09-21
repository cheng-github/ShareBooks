package usst.edu.cn.sharebooks.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class NetWorkUtil {
    /**
     * 判断网络是否连接
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network network : networks) {
                networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if (networkInfos != null) {
                    for (NetworkInfo networkInfo : networkInfos) {
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
