package usst.edu.cn.sharebooks.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.UpdateUserInfoEvent;
import usst.edu.cn.sharebooks.model.user.UpdateUserInfoResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.ApiInterface;
import usst.edu.cn.sharebooks.ui.activity.BookStallActivity;
import usst.edu.cn.sharebooks.ui.activity.DonateStallActivity;
import usst.edu.cn.sharebooks.ui.activity.OrderBookActivity;
import usst.edu.cn.sharebooks.ui.activity.UserSettingActivity;
import usst.edu.cn.sharebooks.util.RxUtil;


public class MainThirdFragment extends BaseFragment {
    private static String Section_Number="section_number";
    View rootView=null;
    private LinearLayout mLL1;
    private LinearLayout mLL2;
    private LinearLayout mLL3;
    private LinearLayout mLL4;
    private ImageView mSex;
    private CardView mCard;
    private TextView mNickName;
    private TextView mUserName;
    private TextView mContri;
    private TextView mGet;
    private User mUser;
    private ImageView mTouxiang;

    public static MainThirdFragment newInstance(User user){
        MainThirdFragment thirdFragment = new MainThirdFragment();
        Bundle args = new Bundle();
        args.putInt(Section_Number,1);
        args.putSerializable("UserData",user);
        thirdFragment.setArguments(args);
        return thirdFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TestLifeCycle","..............onCreate() from ............. MainThirdFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TestLifeCycle","..............onCreateView() from ............. MainThirdFragment");
        rootView = inflater.inflate(R.layout.main_acrivity_fragment,container,false);
        if (getArguments().getInt(Section_Number) == 1){
            rootView = inflater.inflate(R.layout.third_fragment,container,false);
            mLL1 = (LinearLayout)rootView.findViewById(R.id.my_lib);
            mLL2 = (LinearLayout)rootView.findViewById(R.id.my_donate);
            mLL3 = (LinearLayout)rootView.findViewById(R.id.gotta_book);
            mLL4 = (LinearLayout)rootView.findViewById(R.id.order_book);
            mSex = (ImageView)rootView.findViewById(R.id.sex);
            mCard = (CardView)rootView.findViewById(R.id.card);
            mNickName = (TextView)mCard.findViewById(R.id.mynickname);
            mUserName = (TextView)rootView.findViewById(R.id.myuserName);
            mContri = (TextView)rootView.findViewById(R.id.mycontri);
            mGet = (TextView)rootView.findViewById(R.id.myget);
            mTouxiang = (ImageView)rootView.findViewById(R.id.myImage);
            this.mUser = (User)getArguments().getSerializable("UserData");
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLifeCycle","..............onViewCreated() from ............. MainThirdFragment");
        initView();
    }

    private void initView(){
        //初始化card的值
        mNickName.setText(mUser.getNickName());
        mUserName.setText("用户名:"+mUser.getUserName());
        mContri.setText("贡献书籍:"+mUser.getContri_count());
        mGet.setText("交易书籍:"+mUser.getGet_count());
        if (mUser.getSex().equals("?")){
            mSex.setVisibility(View.INVISIBLE);
        }else if (mUser.getSex().equals("男")){
            mSex.setImageResource(R.drawable.male);
        }else if (mUser.equals("女")){
            mSex.setImageResource(R.drawable.female);
        }
        if (mUser.getImageUrl().equals("?")){
            Glide.with(this).load(R.drawable.touxiang).into(mTouxiang);
        }else {
            String imageUrl = ApiInterface.UserImageUrl+mUser.getImageUrl();
            GlideApp.with(this).load(imageUrl).into(mTouxiang);
        }
        //下面是设置我的页面的四个点击响应事件
        mLL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookStallActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("User",mUser);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        //打开编辑个人信息界面....
        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserSettingActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("User",mUser);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        mLL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DonateStallActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("User",mUser);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        mLL4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderBookActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("User",mUser);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
    }

    private void updateUserInfo(User user){
        //其实每次修改了用户信息，只修改数据的引用是远远不够的，还要这样每个地方都要手动修改重新调用
        this.mUser = user;
        mNickName.setText(mUser.getNickName());
        if (mUser.getSex().equals("?")){
            mSex.setVisibility(View.INVISIBLE);
        }else if (mUser.getSex().equals("男")){
            mSex.setImageResource(R.drawable.male);
        }else if (mUser.getSex().equals("女")){
            mSex.setImageResource(R.drawable.female);
        }
        if (mUser.getImageUrl().equals("?")){
            GlideApp.with(this).load(R.drawable.touxiang).into(mTouxiang);
        }else {
            String imageUrl = ApiInterface.UserImageUrl+mUser.getImageUrl();
            GlideApp.with(this).load(imageUrl).into(mTouxiang);
        }
        Log.i("TestCamera","updateUserInfo(User user)...........from third Framgment.........");
    }

    private Test mTest;

    public void setInterface(Test test){
        mTest = test;
    }

    public interface Test{
        void test();
    }

    @Override
    protected void loadWhenVisible() {
       // setMyTitle.setTitle();
        //这里调用会空指针是不是生命周期的问题
        //设置标题
       // mTest.test();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("TestLifeCycle","..............onAttach() from ............. MainThirdFragment");
    }

    @Override
    public void onStart() {
        super.onStart();
        RxBus.getInstance().tObservable(UpdateUserInfoEvent.class)
                .doOnNext(new Consumer<UpdateUserInfoEvent>() {
                    @Override
                    public void accept(@NonNull UpdateUserInfoEvent updateUserInfoEvent) throws Exception {
                        updateUserInfo(updateUserInfoEvent.getUser());
                    }
                })
                .compose(RxUtil.<UpdateUserInfoEvent>io())
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();
        Log.i("TestLifeCycle","..............onStart() from ............. MainThirdFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TestLifeCycle","..............onResume() from ............. MainThirdFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("TestLifeCycle","..............onPause() from ............. MainThirdFragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("TestLifeCycle","..............onStop() from ............. MainThirdFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("TestLifeCycle","..............onDestroyView() from ............. MainThirdFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TestLifeCycle","..............onDestroy() from ............. MainThirdFragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("TestLifeCycle","..............onDetach() from ............. MainThirdFragment");
    }
}
