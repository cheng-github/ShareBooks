package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.articlelist.ArticleHeader.SimpleArticle;
import usst.edu.cn.sharebooks.model.event.OpenArticleDetailEvent;


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
        ((SimpleArticleViewHolder)holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 :list.size();
    }

    public void setList(List<SimpleArticle> list) {
        this.list = list;
    }

    class SimpleArticleViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private TextView mAuthor;
        private ImageView mImage;
        private TextView mForward;
        private TextView mTime;
        private View item;

        public SimpleArticleViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            mTitle = itemView.findViewById(R.id.tv_title);
            mAuthor = itemView.findViewById(R.id.tv_author);
            mImage = itemView.findViewById(R.id.iv_pic);
            mForward = itemView.findViewById(R.id.tv_forward);
            mTime = itemView.findViewById(R.id.tv_time);
        }

        private void bind(final SimpleArticle simpleArticle){
            mTitle.setText(simpleArticle.data.content_list[1].title);
            if (simpleArticle.data.content_list[1].author.user_name != null)
            mAuthor.setText("文 / "+simpleArticle.data.content_list[1].author.user_name);
            Glide.with(context).load(simpleArticle.data.content_list[1].img_url).into(mImage);
            mForward.setText(simpleArticle.data.content_list[1].forward);
            mTime.setText(simpleArticle.data.date.split(" ")[0]);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RxBus.getInstance().post(new OpenArticleDetailEvent().setArticle_id(simpleArticle.data.content_list[1].item_id));
                }
            });
        }
    }
}
