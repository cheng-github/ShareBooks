package usst.edu.cn.sharebooks.ui.fragment;

import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.donate.AllAvailableBook;
import usst.edu.cn.sharebooks.model.donate.GivenBookItem;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.GivenBookAdapter;


public class MainSecondFragment extends BaseFragment {
    private static String Section_Number="section_number";
    private View rootView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private GivenBookAdapter adapter;
    //Data
    List<GivenBookItem> givenBookItems = new ArrayList<>();

    public static MainSecondFragment newInstance(){
        MainSecondFragment secondFragment = new MainSecondFragment();
        Bundle args = new Bundle();
        args.putInt(Section_Number,1);
        secondFragment.setArguments(args);
        return secondFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TestLifeCycle","onCreateView() from MainSecondeFragment");
        rootView = inflater.inflate(R.layout.main_acrivity_fragment,container,false);
        if (getArguments().getInt(Section_Number) == 1){
            rootView = inflater.inflate(R.layout.second_fragment,container,false);
            mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiprefresh);
            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview);
            mImageView = (ImageView)rootView.findViewById(R.id.iv_erro);
        }
        return rootView;
    }

    //这个方法是在view layout视图加载后才调用的，也就是 onCreateView(...)方法之后
    //无返回值
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLifeCycle","onViewCreated() from MainSecondFragment");
        initView();//是否在调用这个方法前加判断??
    }

    private void initView(){
        if (mRefreshLayout != null){
            //设置加载的动态颜色
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            mRefreshLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                //    load();//匿名内部类可以自由的调用其外部类的方法
                                }
                            },1000);
                        }
                    });
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter = new GivenBookAdapter(givenBookItems);
        adapter = new GivenBookAdapter(new GivenBookAdapter.OrderAction() {
            @Override
            public void order(GivenBookItem item) {

            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void loadWhenVisible() {
           Log.i("TestLifeCycle","loadWhenVisible()");
//            load("all","all");
 //       RxBus.getInstance().post(new ChangeTitleEvent());
    }

}
