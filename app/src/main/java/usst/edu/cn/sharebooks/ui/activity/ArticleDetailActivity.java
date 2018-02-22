package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.articlelist.ArticleContent.ArticleDetail;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;


public class ArticleDetailActivity extends BaseActivity {
    private String item_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail);
        item_id =  getIntent().getStringExtra("ArticleId");//获取传递过来的文章int的值
        loadData();
    }


    private void loadData(){
        Log.i("TestArticle",item_id);
        RetrofitSingleton.getInstance().loadArticleContent(item_id)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error","加载文章内容出错");
                    }
                })
                .doOnNext(new Consumer<ArticleDetail>() {
                    @Override
                    public void accept(ArticleDetail articleDetail) throws Exception {
                        Log.i("TestArticle",articleDetail.data.hp_content);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }
}
