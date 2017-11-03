package usst.edu.cn.sharebooks.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.sellstall.AllUserSellStallResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.UserSellBookStallAdapter;
import usst.edu.cn.sharebooks.util.DialogUtil;


public class FirstPagerSecondFragment extends BaseFragment {
    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private UserSellBookStallAdapter mAdapter;
    private boolean isManuRefresh = false;
    private AllUserSellStallResponse mAllUserSellStallResponse;
    private User user;

    public static FirstPagerSecondFragment newInstance(User user){
        FirstPagerSecondFragment firstPagerSecondFragment = new FirstPagerSecondFragment();
        Bundle args = new Bundle();
        args.putSerializable("User",user);
        firstPagerSecondFragment.setArguments(args);
        return firstPagerSecondFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.firstpager_secondfragment,container,false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_swiperefresh);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.rv_sellstalllist);
        this.user = (User)getArguments().getSerializable("User");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadData();
    }

    private void initView(){
        //设置加载的动态颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isManuRefresh = true;
                                loadData();
                            }
                        },1000);
                    }
                });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new UserSellBookStallAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData(){
        RetrofitSingleton.getInstance().getAllUserSellStallInformaiton()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if (isManuRefresh){
                            mSwipeRefreshLayout.setRefreshing(true);
                        }else {
                            DialogUtil.showTriangleDialogForLoading(getActivity(),"加载中...");
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                })
                .doOnNext(new Consumer<AllUserSellStallResponse>() {
                    @Override
                    public void accept(@NonNull AllUserSellStallResponse allUserSellStallResponse) throws Exception {
                        mAllUserSellStallResponse = allUserSellStallResponse;
                        mAdapter.setList(mAllUserSellStallResponse.userSellStalls);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (isManuRefresh){
                            mSwipeRefreshLayout.setRefreshing(false);
                        }else {
                            DialogUtil.hideDialogForLoading(getActivity());
                        }
                    }
                })
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();
    }

    @Override
    protected void loadWhenVisible() {

    }
}
