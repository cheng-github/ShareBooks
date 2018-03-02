package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.OpenHistoryOrderEvent;
import usst.edu.cn.sharebooks.model.historyorders.HistoryOrderItem;
import usst.edu.cn.sharebooks.network.ApiInterface;


public class HistoryOrdersAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<HistoryOrderItem> lists;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new HistoryOrdersViewHolder(LayoutInflater.from(context).inflate(R.layout.historyorders,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  HistoryOrdersViewHolder)
            ((HistoryOrdersViewHolder)holder).bind(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists == null?0:lists.size();
    }

    class HistoryOrdersViewHolder extends RecyclerView.ViewHolder{
        private CardView mCardView;
        private ImageView mImageView;
        private TextView mBookName;
        private TextView mAuthor;
        private TextView mPreservation;
        private TextView mName;
        private TextView mOrderDate;
        private TextView mFinishDate;

        public HistoryOrdersViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.cv_history);
            mImageView = itemView.findViewById(R.id.iv_bookimage);
            mBookName = itemView.findViewById(R.id.tv_bookName);
            mAuthor = itemView.findViewById(R.id.tv_author);
            mPreservation = itemView.findViewById(R.id.tv_pressversion);
            mName = itemView.findViewById(R.id.tv_name);
            mOrderDate = itemView.findViewById(R.id.tv_orderdate);
            mFinishDate = itemView.findViewById(R.id.tv_finishdate);
        }

        private void bind(final HistoryOrderItem item){
            mBookName.setText(item.bookName);
            mAuthor.setText(item.author);
            mPreservation.setText(item.press+"/"+item.version);
            if (item.isJiaoCai == 1)
                Glide.with(context).load(ApiInterface.AllBookImageUrl+item.bookImageUrl).into(mImageView);
            else
                Glide.with(context).load(item.bookImageUrl).into(mImageView);
            mName.setText("交易对象:"+item.userInfo.nickName);
            mOrderDate.setText("下单时间:"+item.orderDate.substring(0,item.orderDate.length()-2));
            mFinishDate.setText("交易完成:"+item.finishTime.substring(0,item.orderDate.length()-2));
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //查看订单详情的意义不大，我们仅仅显示交易对象的信息足矣
                    RxBus.getInstance().post(new OpenHistoryOrderEvent(item.userInfo));
                }
            });
        }
    }

    public void setLists(List<HistoryOrderItem> lists) {
        this.lists = lists;
    }
}
