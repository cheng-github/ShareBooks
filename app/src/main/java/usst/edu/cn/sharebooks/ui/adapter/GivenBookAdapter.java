package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.donate.GivenBookItem;
import usst.edu.cn.sharebooks.model.event.OpenNormalBookDetailEventForDonate;
import usst.edu.cn.sharebooks.model.event.normalbookmodel.NormalBookData;
import usst.edu.cn.sharebooks.network.ApiInterface;

/**
 * 这是第二个fragment recyclerView的adapter
 * Created by Cheng on 2017/9/23.
 */

public class GivenBookAdapter extends RecyclerView.Adapter {
    private List<GivenBookItem> allBooks = new ArrayList<>();
    private Context context;
    private OrderAction orderAction;

    public GivenBookAdapter(OrderAction orderAction){
        this.orderAction = orderAction;
    }

    //如果每个item需要样式不一样 那么就需要重写这个方法
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();//获得当前这个界面的context..
        return new GivenBookViewHolder(LayoutInflater.from(context).inflate(R.layout.given_bookitem,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((GivenBookViewHolder)holder).bind(allBooks.get(position));//传入对应的书籍信息
    }

        @Override
        public int getItemCount() {
            return allBooks.size();
        }

        class GivenBookViewHolder extends RecyclerView.ViewHolder{
            private ImageView mImageView;
            private TextView mBookName;
            private TextView mAuthorPressVersion;
            private TextView mBookDesc;
            private TextView mDonateNickName;
            private ImageView mSex;
            private Button mOrder;
            private CardView mCard;

        public GivenBookViewHolder(View itemView){
            super(itemView);
            mImageView =(ImageView)itemView.findViewById(R.id.bookImage);
            mBookName = (TextView)itemView.findViewById(R.id.bookName);
            mAuthorPressVersion = (TextView)itemView.findViewById(R.id.tv_authorpressversion);
            mBookDesc = (TextView)itemView.findViewById(R.id.bookDesc);
            mDonateNickName = (TextView)itemView.findViewById(R.id.tv_nickName);
            mSex = (ImageView) itemView.findViewById(R.id.iv_sex);
            mOrder = (Button) itemView.findViewById(R.id.bt_order);
            mCard = (CardView)itemView.findViewById(R.id.cv_card);
        }

        private void bind(final GivenBookItem item){
            String imageUrl = "";
            if (item.isJiaoCai == 1){
                 imageUrl =ApiInterface.AllBookImageUrl+item.imageUrl;
            }else {
                imageUrl = item.imageUrl;
            }
            Glide.with(context).load(imageUrl).into(mImageView);
            mBookName.setText(item.bookName);
            mAuthorPressVersion.setText(item.author+"/"+item.press+"/"+item.version);
            mBookDesc.setText(item.bookIntro);
            mDonateNickName.setText(item.nickName);
            if (item.sex.equals("?")){
                mSex.setVisibility(View.INVISIBLE);
            }else if (item.sex.equals("男")){
                mSex.setImageResource(R.drawable.male);
            }else if (item.sex.equals("女")){
                mSex.setImageResource(R.drawable.female);
            }
            mOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    materialDialog.setMessage("确认要预定这本书吗?")
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    orderAction.order(item);
                                    materialDialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                }
                            })
                            .setCanceledOnTouchOutside(true)
                            .show();
                }
            });
            mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenNormalBookDetailEventForDonate bookData = new OpenNormalBookDetailEventForDonate();
                    NormalBookData normalBookData = new NormalBookData();
                    normalBookData.setBookName(item.bookName);
                    normalBookData.setAuthor(item.author);
                    normalBookData.setPress(item.press);
                    normalBookData.setVersion(item.version);
                    normalBookData.setBookIntro(item.bookIntro);
                    bookData.setNormalBookData(normalBookData);
                    bookData.setImageView(mImageView);
                    Bitmap bitmap = null;
                    BitmapDrawable bitmapDrawable =((BitmapDrawable) mImageView.getDrawable());
                    if (bitmapDrawable != null){
                        bitmap =  bitmapDrawable.getBitmap();
                    }
                    bookData.setBitmap(bitmap);
                    RxBus.getInstance().post(bookData);
                }
            });
        }
    }

    public void setList(List<GivenBookItem> list){
        this.allBooks = list;
    }

    public interface OrderAction{
        void order(GivenBookItem item);
    }
}
