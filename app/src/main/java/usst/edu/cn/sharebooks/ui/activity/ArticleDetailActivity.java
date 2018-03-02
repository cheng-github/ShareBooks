package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.articlelist.ArticleContent.ArticleDetail;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.util.AnimationUtils;
import usst.edu.cn.sharebooks.util.DialogUtil;


public class ArticleDetailActivity extends BaseActivity {
    private String item_id;
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mContent;
    private ArticleDetail content;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail);
        item_id =  getIntent().getStringExtra("ArticleId");//获取传递过来的文章int的值
        initView();
        loadData();
    }

    private void initView(){
        mTitle = (TextView) this.findViewById(R.id.tv_title);
        mAuthor = (TextView)this.findViewById(R.id.tv_author);
        mContent = (TextView)this.findViewById(R.id.tv_content);
        linearLayout = (LinearLayout)this.findViewById(R.id.ll_root);
//        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    private void loadData(){
        Log.i("TestArticle",item_id);
        DialogUtil.showTriangleDialogForLoading(this,"加载中..");
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
                        content = articleDetail;
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //刷新UI界面并关闭动画
                        DialogUtil.hideDialogForLoading(ArticleDetailActivity.this);
                        mTitle.setText(content.data.hp_title);
                        mAuthor.setText(" 文 / "+content.data.hp_author);
                        //我们需要把文章内容里的<p>替换成一个空串
//                        content.data.hp_content = content.data.hp_content.replaceAll("[<][p]{1}[a-zA-Z0-9_\"]{0,}[>]","");
                        content.data.hp_content = content.data.hp_content.replaceAll("[<][p][^>]{0,}[>]","");
                        content.data.hp_content = content.data.hp_content.replaceAll("[<][/]{1}[p]{1}[>]","");  //这里主要用于注释掉段落的结尾
                        content.data.hp_content = content.data.hp_content.replaceAll("[<][/br]{0,}[>]","\n");//替换掉换行符<br>或者</br>
                        content.data.hp_content = content.data.hp_content.replaceAll("[<][/strong]{0,}[>]","");//替换掉可能存在的字体加粗标记
                        mContent.setText(content.data.hp_content);
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }
}
