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
    public static final int TYPE_NORMAL = 0x1;
    private static final int TYPE_FOOTER = 0x2;
    public LayoutInflater layoutInflater;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(context);
        if (viewType == TYPE_NORMAL)
        return new SimpleArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_article_layout,parent,false));
        else if (viewType == TYPE_FOOTER ){
            View view  = layoutInflater.inflate(R.layout.recycler_footer,parent,false);
            return  new FooterViewHolder(view);
        }
//        return new FooterViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_footer,parent,false));
        else  return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SimpleArticleViewHolder)
        ((SimpleArticleViewHolder)holder).bind(list.get(position));//在转换类型之前先进行判断
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 :list.size()+1;
    }

    //用于判断在recyclerview显示的视图类型，也就是说，当我们需要在不同位置显示不同类型的view的时候，需要重写这个方法
    //我尝试过在recyclerview的布局文件里直接添加其他视图，但是并没有显示，那么只能使用这种方式来添加底部提示了
    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);  默认返回值是0,也就是没有重写的时候
        if (position != 3)
            return TYPE_NORMAL;
        else
            return TYPE_FOOTER;
    }

    public void setList(List<SimpleArticle> list) {
        this.list = list;
    }

    //具体文章item的viewholder
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

    //仅仅需要加载视图
    class FooterViewHolder extends RecyclerView.ViewHolder{

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
