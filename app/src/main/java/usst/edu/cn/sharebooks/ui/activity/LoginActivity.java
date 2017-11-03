package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.ui.fragment.LoginFragment;


public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanced){
        Log.i("TestLife",".....onCreate() from LoginActivity.....");
        super.onCreate(savedInstanced);
        setContentView(R.layout.login_activity);
        initView();
    }

    private void initView(){
        Log.i("TestLife",".....initView() from LoginActivity.....");
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.login_mainview,new LoginFragment()).commit();
    }
}
