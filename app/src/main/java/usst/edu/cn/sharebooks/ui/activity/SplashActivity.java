package usst.edu.cn.sharebooks.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.trello.rxlifecycle2.android.ActivityEvent;

import org.litepal.crud.DataSupport;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.user.LoginResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.util.NetWorkUtil;
import usst.edu.cn.sharebooks.util.ToastUtil;

/**
 *
 * Created by Cheng on 2017/5/22.
 */

public class SplashActivity extends BaseActivity {
    private final int SPLASH_DELAY_LENGTH = 1200;
    private int SelectionNumber; //表示选择到登录界面还是主界面
    private User sendUser;
    private boolean isNetConnected;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedIntance){
        super.onCreate(savedIntance);
        setContentView(R.layout.activity_splash);
        Log.i("TestData","未定义的SelectionNumber="+SelectionNumber);
        isNetConnected = NetWorkUtil.isNetWorkConnected(this);
        if (isNetConnected){
            List<User> users= DataSupport.findAll(User.class);//之前 如果登录成功  就使用258作为登录成功的存在数据库的表示id
            if (users.size() == 0){
                //如果之前登录成功，那么就验证登录信息
                SelectionNumber = 0;
                goHome();
            } else if (users.get(0) == null){
                SelectionNumber = 0;
                goHome();
                Log.i("TestData","splashActivity选择了"+SelectionNumber+"");
            }else {
                //访问网络确认登录信息是否有效
                String DbUserName = users.get(0).getUserName();
                String DbPassWord = users.get(0).getPassWord();
                Log.i("TestData","dbUser-"+DbUserName+"\ndbPassWord-"+DbPassWord);
                testLogin(DbUserName,DbPassWord);
                Log.i("TestData","splashActivity选择了"+SelectionNumber+"-----在testLogin()之后");
            }
        }else {
            SelectionNumber = 3;
            Toast.makeText(SplashActivity.this,"网络连接异常，请检查您的网络设置",Toast.LENGTH_SHORT).show();
            goHome();
        }
    }

    //测试之前的登录的信息是否失效
    private void testLogin(String userName,String passWord){
         disposable = RetrofitSingleton.getInstance().setmContext(SplashActivity.this).userLogin(userName,passWord)
                .doOnNext(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(@NonNull LoginResponse loginResponse) throws Exception {
                        Log.i("Test","doOnNext()--------from testLogin");
                       if (loginResponse.status == 1){
                           SelectionNumber = 1;
                           sendUser = loginResponse.user;
                       }else if (loginResponse.status == 0){
                           SelectionNumber = 0;
                       }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("Test","doOnError()--------from testLogin");
                        Toast.makeText(SplashActivity.this,"网络连接超时",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(SplashActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
//                        SelectionNumber = 0;
                        //上面两句没有用 不知道为什么
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("Test","doOnComplete()--------from testLogin");
                        goHome();
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

//    public void timeOutHanding(){
//        if (disposable != null)
//            disposable.dispose();
//        Log.e("Error","连接超时处理调用");
//        //很奇怪为什么吐司没有显示
//        Toast.makeText(SplashActivity.this,"网络连接超时,请检查您的网络",Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private synchronized void  goHome(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("TestData","splashActivity选择了"+SelectionNumber+"");
                switch (SelectionNumber){
                    case 0:
                        Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(loginIntent);
                        finish();//记得调用finish()不然可以返回的
                        break;
                    case 1:
                        Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userInfo",sendUser);
                        mainIntent.putExtras(bundle);
                        startActivity(mainIntent);
                        finish();
                        break;
                    default:
                        Intent defaultIntent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(defaultIntent);
                        finish();
                        break;
                }

            }
        },SPLASH_DELAY_LENGTH);
    }
}
