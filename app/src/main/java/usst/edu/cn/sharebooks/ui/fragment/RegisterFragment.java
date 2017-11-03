package usst.edu.cn.sharebooks.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.user.RegisterResponser;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;


public class RegisterFragment extends BaseFragment {
    private View rootView;
    private EditText userName;
    private EditText passWord1;
    private EditText passWord2;
    private EditText nickName;
    private FloatingActionButton mFab;
    private Button register;
    int registerCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TestLife",".....onCreateView() from RegisterFragment.....");
        rootView = inflater.inflate(R.layout.register_paer,container,false);
        if (rootView != null){
            userName = (EditText) rootView.findViewById(R.id.userName);
            passWord1 = (EditText) rootView.findViewById(R.id.passWord1);
            passWord2 = (EditText) rootView.findViewById(R.id.passWord2);
            nickName = (EditText) rootView.findViewById(R.id.nickName);
            mFab = (FloatingActionButton)rootView.findViewById(R.id.fab2);
            register = (Button) rootView.findViewById(R.id.ensure);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i("TestLife",".....onViewCreated() from RegisterFragment.....");
        super.onViewCreated(view, savedInstanceState);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                //这样我们的回退栈就不会存在之前那个LoginFragment了
                //如果使用下面这个方法...我们在注册界面按返回键的话...就会使得两次返回都会导致到login界面...
                //  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main...,new LoginFragment())....
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String um = userName.getText().toString();
                String ps1 = passWord1.getText().toString();
                String ps2 = passWord2.getText().toString();
                String regex = "[a-zA-Z0-9_]{6,15}";
                boolean isNotEmpty;
                if (um.equals("")|| passWord1.equals("")){
                    isNotEmpty = false;
                }else {
                    isNotEmpty = true;
                }
                Pattern pattern = Pattern.compile(regex);
                boolean first = pattern.matcher(um).matches();//如果满足都是6-15位字母 数字 下划线组成  就是true
                boolean second = pattern.matcher(ps1).matches();
                //third用于判断是否密码一致
                boolean third = ps1.equals(ps2);
                //fourth用于判断是否昵称过长
                boolean fourth = nickName.getText().length()<=12 && nickName.getText().length()>0;
                if (first && second && third && fourth && isNotEmpty){
                    Map<String,String> userInf = new HashMap<String, String>();
                    userInf.put("UserName",um);
                    userInf.put("PassWord",ps1);
                    userInf.put("NickName",nickName.getText().toString());
                    register(userInf);
                  // Toast.makeText(getActivity(),"register()调用成功", Toast.LENGTH_LONG).show();
                }
                else if (!isNotEmpty){
                    Toast.makeText(getActivity(),"账号或密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (!first){
                    Toast.makeText(getActivity(),"用户名必须为6-15位字母 数字 下划线组成",Toast.LENGTH_LONG).show();
                }else if (!second){
                    Toast.makeText(getActivity(),"密码必须为6-15位字母 数字 下划线组成",Toast.LENGTH_LONG).show();
                }else if(!third){
                    Toast.makeText(getActivity(),"密码不一致",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"昵称长度不能超过十二位",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register(Map<String,String> map){
        RetrofitSingleton.getInstance().userRegister(map)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {

                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i("TestData","发生了错误");
                    }
                })
                .doOnNext(new Consumer<RegisterResponser>() {
                    @Override
                    public void accept(@NonNull RegisterResponser registerResponser) throws Exception {
                        registerCode = registerResponser.statusCode;
                        if (registerCode == 2){
                            Toast.makeText(getActivity(),"该账号已注册",Toast.LENGTH_SHORT).show();
                        }else if (registerCode == 0){
                            Toast.makeText(getActivity(),"服务器异常",Toast.LENGTH_SHORT).show();
                        }else if (registerCode == 1){
                            Toast.makeText(getActivity(),"注册成功",Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                        Log.i("TestData","数据传输过来了 statusCode = "+registerResponser.statusCode);
                    }
                })
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();


    }

    @Override
    protected void loadWhenVisible() {

    }
}
