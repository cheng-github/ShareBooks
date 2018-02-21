package usst.edu.cn.sharebooks.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.articlelist.ArticleHeader.SimpleArticle;
import usst.edu.cn.sharebooks.model.articlelist.ArticleIDList;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;


public class MainSecondFragment extends BaseFragment {
    private static String Section_Number="section_number";
    private static boolean isLoad = false;
    private View rootView;
    private GridView mGridView;
    public ArticleIDList articleIDLists;
    public ArrayList<SimpleArticle> list = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    //更新articleIDLists
    public void setArticleIDLists(ArticleIDList articleIDLists) {
        this.articleIDLists = articleIDLists;
//        for (String item:this.articleIDLists.data)
//            Log.i("TestArticle",item);
    }

    public static MainSecondFragment newInstance(){
        MainSecondFragment secondFragment = new MainSecondFragment();
        Bundle args = new Bundle();
        args.putInt(Section_Number,1);
//        args.putSerializable("ArticleID",articleIDLists);
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
//            this.articleIDLists = (ArticleIDList) getArguments().getSerializable("ArticleID");
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLifeCycle","onViewCreated() from MainSecondFragment");
        mSwipeRefreshLayout = view.findViewById(R.id.swiprefresh);
        mRecyclerView = view.findViewById(R.id.rv_article);
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
                                loadData();//匿名内部类可以自由的调用其外部类的方法
                            }
                        },1000);
                    }
                });

    }


    private void loadData(){
        //加载每一个文章的标题，作者，图片，以及第一段文字,以及对于每一篇文章我们需要的id,这个id是用于获取文章的具体内容
        //我们仅仅加载三篇文章的内容,不需要太多，太多好像没有特别大的意义
        //首先先测试一个文章头部的加载
        if (list.size()!=0)
            list.clear();
        /*RetrofitSingleton.getInstance().loadArticleHeader(articleIDLists.data[0])
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error","加载文章头部出错");
                    }
                })
                .doOnNext(new Consumer<SimpleArticle>() {
                    @Override
                    public void accept(SimpleArticle simpleArticle) throws Exception {
                        Log.i("TestArticle",simpleArticle.data.id);
                        for (int i = 0;i<simpleArticle.data.content_list.length;i++){
                            Log.i("TestArticle",simpleArticle.data.content_list[i].item_id);
                            Log.i("TestArticle",simpleArticle.data.content_list[i].title);
                            Log.i("TestArticle",simpleArticle.data.content_list[i].forward);
                            Log.i("TestArticle",simpleArticle.data.content_list[i].img_url);
                            if (simpleArticle.data.content_list[i].author.user_name!=null)
                            Log.i("TestArticle",simpleArticle.data.content_list[i].author.user_name);
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //加载文章完成
                    }
                })
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();*/
        //使用merge一次完成多个请求
        Observable.merge(RetrofitSingleton.getInstance().loadArticleHeader(articleIDLists.data[0]),
                RetrofitSingleton.getInstance().loadArticleHeader(articleIDLists.data[1]),
                RetrofitSingleton.getInstance().loadArticleHeader(articleIDLists.data[2]))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error","加载多个请求出现错误");
                    }
                })
                .doOnNext(new Consumer<SimpleArticle>() {
                    @Override
                    public void accept(SimpleArticle simpleArticle) throws Exception {
                        list.add(simpleArticle);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("TestArticle","所有数据加载完毕");
                        for (SimpleArticle article:list){
                            for (int i = 0;i<article.data.content_list.length;i++){
                                if (i == 1){
                                    Log.i("TestArticle",article.data.content_list[i].item_id);
                                    Log.i("TestArticle",article.data.content_list[i].title);
                                    Log.i("TestArticle",article.data.content_list[i].forward);
                                    Log.i("TestArticle",article.data.content_list[i].img_url);
                                    if (article.data.content_list[i].author.user_name!=null)
                                        Log.i("TestArticle",article.data.content_list[i].author.user_name);
                                }
                            }
                        }
                    }
                })
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();
    }

    @Override
    protected void loadWhenVisible() {
           Log.i("TestLifeCycle","loadWhenVisible()");
//            load("all","all");
 //       RxBus.getInstance().post(new ChangeTitleEvent());
        if (!isLoad){
            isLoad = true;
        }
        loadData();
    }

}
