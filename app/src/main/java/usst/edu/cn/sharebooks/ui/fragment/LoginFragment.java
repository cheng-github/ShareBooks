package usst.edu.cn.sharebooks.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.trello.rxlifecycle2.android.FragmentEvent;
import org.litepal.crud.DataSupport;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.user.LoginResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.activity.MainActivity;


public class LoginFragment extends BaseFragment {
    private View rootView;
    private EditText mUserName;
    private EditText mPassWord;
    private Button mLogin;
    private FloatingActionButton mFab;
    private LoginResponse mResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TestLife",".....onCreateView() from LoginFragment.....");
        rootView = inflater.inflate(R.layout.login_pager,container,false);
        mUserName = (EditText) rootView.findViewById(R.id.userName);
        mPassWord = (EditText) rootView.findViewById(R.id.passWord);
        mLogin = (Button) rootView.findViewById(R.id.login);
        mFab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLife",".....onViewCreated() from LoginActivity.....");
        initView();
    }

    private void initView(){
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_mainview,new RegisterFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserName.getText().toString();
                String passWord = mPassWord.getText().toString();
                if (userName.equals("")){
                    Toast.makeText(getActivity(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if (passWord.equals("")){
                    Toast.makeText(getActivity(),"密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    login(userName,passWord);
                }
            }
        });
    }

    private void login(String userName,String passWord){
        Log.i("TestData","调用到乐这儿");
        RetrofitSingleton.getInstance().userLogin(userName,passWord)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.i("TestData","doOnSubcribe from login()");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i("TestData","doOnError from login()");
                        Toast.makeText(getActivity(),"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(@NonNull LoginResponse response) throws Exception {
                        Log.i("TestData status:",String.valueOf(response.status));
                        //  Log.i("TestData nickName:",response.user.getNickName());
                        mResponse = response;
                        if (mResponse.status == 1){
                            loginSucceed();
                        }else if (mResponse.status == 0){
                            Toast.makeText(getActivity(),"账号或密码不正确",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();
    }

    private void loginSucceed(){
        Toast.makeText(getActivity(),"登录成功",Toast.LENGTH_SHORT).show();
        Log.i("TestData nickName:","nickName :"+mResponse.user.getNickName()+
                "\nbookCount :"+mResponse.user.getContri_count()+"&"+mResponse.user.getGet_count());
        //将账号密码写入数据库用于下次登录
        //如果已经有一个用户...但是出现退出登录..或者是修改密码什么的...就需要重新验证了
        //这时候我们需要将之前的那个数据库里的user删除掉
        User dbInfo = null;
        if (DataSupport.findAll(User.class) != null){
            DataSupport.deleteAll(User.class);//删除掉之前存在的user
        }
        dbInfo = new User();
        dbInfo.setId(mResponse.user.getId());
        dbInfo.setUserName(mResponse.user.getUserName());
        dbInfo.setPassWord(mResponse.user.getPassWord());
        dbInfo.save();

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        //将从服务器端得到的user的数据传到mainActivity里去
        User userInfo = mResponse.user;
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo",userInfo);
        mainIntent.putExtras(bundle);
        startActivity(mainIntent);
        getActivity().finish();
    }

    @Override
    protected void loadWhenVisible() {

    }
}
