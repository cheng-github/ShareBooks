package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.model.articlelist.ArticleHeader.SimpleArticle;


public class SimpleArticleAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<SimpleArticle> list = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SimpleArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_article_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 :list.size();
    }

    public void setList(List<SimpleArticle> list) {
        this.list = list;
    }

    class SimpleArticleViewHolder extends RecyclerView.ViewHolder{

        public SimpleArticleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
