package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.OpenNormalBookDetailEventForSell;
import usst.edu.cn.sharebooks.model.event.OpenSellStallDetailEvent;
import usst.edu.cn.sharebooks.model.event.normalbookmodel.NormalBookData;
import usst.edu.cn.sharebooks.model.sellstall.UserSellStallItemForOthers;
import usst.edu.cn.sharebooks.network.ApiInterface;


public class UserSellStallItemAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<UserSellStallItemForOthers> list = new ArrayList<>();
    private OrderAction orderAction;

    public UserSellStallItemAdapter(OrderAction orderAction) {
        this.orderAction = orderAction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new UserSellStallItemVH(LayoutInflater.from(context).inflate(R.layout.sell_bookitem,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UserSellStallItemVH)holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<UserSellStallItemForOthers> list){
        this.list = list;
    }

    private class UserSellStallItemVH extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        private TextView mBookName;
        private TextView mAuthorPressVersion;
        private TextView mBookDesc;
        private Button mOrder;
        private CardView mCardView;

        public UserSellStallItemVH(View itemView) {
            super(itemView);
            mImageView =(ImageView)itemView.findViewById(R.id.bookImage);
            mBookName = (TextView)itemView.findViewById(R.id.bookName);
            mAuthorPressVersion = (TextView)itemView.findViewById(R.id.tv_authorpressversion);
            mBookDesc = (TextView)itemView.findViewById(R.id.bookDesc);
            mOrder = (Button) itemView.findViewById(R.id.bt_order);
            mCardView = (CardView)itemView.findViewById(R.id.cv_book);
        }

        private void bind(final UserSellStallItemForOthers item){
            String imageUrl = "";
            if (item.isJiaoCai == 1){
                imageUrl = ApiInterface.AllBookImageUrl+ Uri.encode(item.imageUrl);
            }else {
                imageUrl = item.imageUrl;
            }
            Glide.with(context).asBitmap().load(imageUrl).into(mImageView);
            mBookName.setText(item.bookName);
            mAuthorPressVersion.setText(item.author+"/"+item.press+"/"+item.version);
            mBookDesc.setText(item.bookIntro);
            mOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderAction.order(item);
                }
            });
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NormalBookData normalBookData = new NormalBookData();
                    normalBookData.setBookName(item.bookName);
                    normalBookData.setAuthor(item.author);
                    normalBookData.setPress(item.press);
                    normalBookData.setVersion(item.version);
                    normalBookData.setBookIntro(item.bookIntro);
                    OpenNormalBookDetailEventForSell openNormalBookDetailEventForSell = new OpenNormalBookDetailEventForSell();
                    openNormalBookDetailEventForSell.setNormalBookData(normalBookData);
                    Bitmap bitmap = null;
                    BitmapDrawable bitmapDrawable =((BitmapDrawable) mImageView.getDrawable());
                    if (bitmapDrawable != null){
                        bitmap =  bitmapDrawable.getBitmap();
                    }
                    openNormalBookDetailEventForSell.setBitmap(bitmap);
                    openNormalBookDetailEventForSell.setImageView(mImageView);
                    RxBus.getInstance().post(openNormalBookDetailEventForSell);
                }
            });
        }
    }

    public interface OrderAction{
        void order(UserSellStallItemForOthers item);
    }
}
