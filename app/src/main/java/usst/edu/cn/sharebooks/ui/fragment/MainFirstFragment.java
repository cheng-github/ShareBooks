package usst.edu.cn.sharebooks.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.ui.adapter.FirstPagerAdapter;

/**
 * 主界面的viewpager每个fragment的加载
 * Created by Cheng on 2017/7/11.
 */

public class MainFirstFragment extends BaseFragment{
    private static String Section_Number="section_number";
    View rootView=null;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    private User user;

    public MainFirstFragment(){
        Log.i("TestLifeCycle","..................MainFirstFragment()构造方法...............");
    }

    //使用newInstance去有选择的加载我们需要的fragment布局文件
    //两次加载避免加载失败
    public static MainFirstFragment newInstance(User user){
        MainFirstFragment mainFirstFragment = new MainFirstFragment();
        Bundle args = new Bundle();
        args.putInt(Section_Number,1);
        args.putSerializable("User",user);
        mainFirstFragment.setArguments(args);
        return mainFirstFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        Log.i("TestLifeCycle","..............onCreateView() from MainFirstFragment.................");
        int section_number = getArguments().getInt(Section_Number);
        rootView = inflater.inflate(
                R.layout.main_acrivity_fragment, container, false);
        if (section_number == 1){
            rootView = inflater.inflate(R.layout.first_fragment,container,false);
            mViewPager = (ViewPager)rootView.findViewById(R.id.viewpager);
            mTabLayout = (TabLayout)rootView.findViewById(R.id.tab_layout);
            user = (User)getArguments().getSerializable("User");
        }
        return rootView;
}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLifeCycle","..............onViewCreated() from MainFirstFragment.................");
        initView();
    }

    private void initView(){
        Log.i("TestLifeCycle","initView() from MainFirstFragment");
        FirstPagerAdapter pagerAdapter = new FirstPagerAdapter(getChildFragmentManager());
//        pagerAdapter.addTab(new FirstPagerFirstFragment(),"免费书籍分享");
//        pagerAdapter.addTab(new FirstPagerSecondFragment(),"校园个人书摊");
        pagerAdapter.addTab(FirstPagerFirstFragment.newInstance(user),"免费书籍分享");
        pagerAdapter.addTab(FirstPagerSecondFragment.newInstance(user),"校园个人书摊");
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void loadWhenVisible() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("TestLifeCycle","..............onStart() from MainFirstFragment.................");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("TestLifeCycle","..............onAttach() from MainFirstFragment.................");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TestLifeCycle","..............onResume() from MainFirstFragment.................");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("TestLifeCycle","..............onPause() from MainFirstFragment.................");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("TestLifeCycle","..............onStop() from MainFirstFragment.................");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("TestLifeCycle","..............onDestroyView() from MainFirstFragment.................");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TestLifeCycle","..............onDestroy() from MainFirstFragment.................");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("TestLifeCycle","..............onDetach() from MainFirstFragment.................");
    }
}

