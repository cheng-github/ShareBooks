package usst.edu.cn.sharebooks.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
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

    public static MainSecondFragment newInstance(ArticleIDList articleIDLists){
        MainSecondFragment secondFragment = new MainSecondFragment();
        Bundle args = new Bundle();
        args.putInt(Section_Number,1);
        args.putSerializable("ArticleID",articleIDLists);
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
            this.articleIDLists = (ArticleIDList) getArguments().getSerializable("ArticleID");
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLifeCycle","onViewCreated() from MainSecondFragment");
    }


    private void loadData(){
        //加载每一个文章的标题，作者，图片，以及第一段文字,以及对于每一篇文章我们需要的id,这个id是用于获取文章的具体内容
        //我们仅仅加载三篇文章的内容,不需要太多，太多好像没有特别大的意义
        //首先先测试一个文章头部的加载
        RetrofitSingleton.getInstance().loadArticleHeader(articleIDLists.data[0])
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error","加载文章头部出错");
                    }
                })
                .doOnNext(new Consumer<SimpleArticle>() {
                    @Override
                    public void accept(SimpleArticle simpleArticle) throws Exception {
                        Log.i("TestArticle",simpleArticle.data.date);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //加载文章完成
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
