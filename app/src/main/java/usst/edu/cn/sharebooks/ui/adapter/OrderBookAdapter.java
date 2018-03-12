package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import me.drakeet.materialdialog.MaterialDialog;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.order.PersonalOrderItem;
import usst.edu.cn.sharebooks.network.ApiInterface;
import usst.edu.cn.sharebooks.ui.activity.OrderDetailActivity;


public class OrderBookAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<PersonalOrderItem> orderList = new ArrayList<>();
    private OrderAction orderAction;
    private BaseActivity mActivity;
    public static final int TypeOrder = 1;//1代表预订
    public static final int TypeOrdered = 2;//表示被预定

    public OrderBookAdapter(OrderAction orderAction,BaseActivity baseActivity) {
        this.orderAction = orderAction;
        this.mActivity = baseActivity;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("TestType",orderList.get(position).isOreder);
        if (orderList.get(position).isOreder.equals("已预订")){
            return OrderBookAdapter.TypeOrder;
        }
        if (orderList.get(position).isOreder.equals("被预订")){
            return OrderBookAdapter.TypeOrdered;
        }
        Log.i("TestType","到底执行了这里没有我曹并且position="+position+"还有orderList.size()"+orderList.size());
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        Log.i("TestType",viewType+"");
        if (viewType == OrderBookAdapter.TypeOrdered)
               return new OrderedBookItemViewHolder(LayoutInflater.from(context).inflate(R.layout.ordereditem_layout,parent,false));
        if (viewType == OrderBookAdapter.TypeOrder)
               return  new OrderBookItemViewHolder(LayoutInflater.from(context).inflate(R.layout.orderitem_layout,parent,false));
    //    return new OrderedBookItemViewHolder(LayoutInflater.from(context).inflate(R.layout.ordereditem_layout,parent,false));//不能设为空
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == TypeOrdered)
        ((OrderedBookItemViewHolder)holder).bind(orderList.get(position));
        if (itemType == TypeOrder)
         ((OrderBookItemViewHolder)holder).bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class OrderedBookItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView mBookImage;
        private TextView mBookName;
        private TextView mBookState;
        private TextView mAuthor;
        private TextView mPressVerison;
        private TextView mBrifeContent;
        private Button mSuccess;
        private Button mFail;
        private CardView mCardView;

        public OrderedBookItemViewHolder(View itemView) {
            super(itemView);
            mBookImage = (ImageView)itemView.findViewById(R.id.iv_bookimage);
            mBookName = (TextView)itemView.findViewById(R.id.tv_bookName);
            mBookState = (TextView)itemView.findViewById(R.id.tv_state);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            mPressVerison = (TextView)itemView.findViewById(R.id.tv_pressversion);
            mBrifeContent = (TextView)itemView.findViewById(R.id.tv_bookIntro);
            mSuccess = (Button)itemView.findViewById(R.id.bt_success);
            mFail = (Button)itemView.findViewById(R.id.bt_fail);
            mCardView = (CardView)itemView.findViewById(R.id.cv_orderitem);
        }

        private void bind(final PersonalOrderItem item){
            String pressversion = item.press+"/"+item.version;
            String imageUrl = "";
            if (item.isJiaoCai == 1){
                imageUrl= ApiInterface.AllBookImageUrl+ Uri.encode(item.imagerUrl);//如果等于1 那么是教材  需要拼接图片地址
            }else {
                imageUrl = item.imagerUrl;
            }
            Glide.with(context).load(imageUrl).into(mBookImage);
            mBookName.setText(item.bookName);
            mBookState.setText(item.isOreder);//如果是被预定 那么不给设置成功失败的权限
            mAuthor.setText(item.author);
            mPressVerison.setText(pressversion);
            mBrifeContent.setText(item.bookIntro);
            mSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //    Log.i("TestNormal","item.bookName---------from recycler"+item.bookName);
                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    if (item.orderType == 1){
                        materialDialog.setMessage("设置贡献结果为成功吗?")
                                .setPositiveButton("确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        orderAction.dealAction(1,item);
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
                    }else if (item.orderType == 2){
                        materialDialog.setMessage("设置交易结果为成功吗?")
                                .setPositiveButton("确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        orderAction.dealAction(1,item);
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
                }
            });
            mFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    if (item.orderType == 1){
                        materialDialog.setMessage("设置贡献结果为失败吗?")
                                .setPositiveButton("确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        orderAction.dealAction(2,item);
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
                    }else if (item.orderType == 2){
                        materialDialog.setMessage("设置交易结果为失败吗?")
                                .setPositiveButton("确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        orderAction.dealAction(2,item);
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
                }
            });
            //设置CardView的点击事件才会生效
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TestActivity","item.setOnClickListener(new View.OnClickListener()");
                    Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("OrderItem",item);
                    //好像..不需要其它信息了...
                    intent.putExtras(args);
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    public class OrderBookItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView mBookImage;
        private TextView mBookName;
        private TextView mBookState;
        private TextView mAuthor;
        private TextView mPressVerison;
        private TextView mBrifeContent;
        private CardView mCardView;

        public OrderBookItemViewHolder(View itemView) {
            super(itemView);
            mBookImage = (ImageView)itemView.findViewById(R.id.iv_bookimage);
            mBookName = (TextView)itemView.findViewById(R.id.tv_bookName);
            mBookState = (TextView)itemView.findViewById(R.id.tv_state);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            mPressVerison = (TextView)itemView.findViewById(R.id.tv_pressversion);
            mBrifeContent = (TextView)itemView.findViewById(R.id.tv_bookIntro);
            mCardView = (CardView)itemView.findViewById(R.id.cv_orderitem);
        }

        private void bind(final PersonalOrderItem item){
            String pressversion = item.press+"/"+item.version;
            String imageUrl = "";
            if (item.isJiaoCai == 1){
                imageUrl= ApiInterface.AllBookImageUrl+Uri.encode(item.imagerUrl);//如果等于1 那么是教材  需要拼接图片地址
            }else {
                imageUrl = item.imagerUrl;
            }
            Glide.with(context).load(imageUrl).into(mBookImage);
            mBookName.setText(item.bookName);
            mBookState.setText(item.isOreder);//如果是被预定 那么不给设置成功失败的权限
            mAuthor.setText(item.author);
            mPressVerison.setText(pressversion);
            mBrifeContent.setText(item.bookIntro);
            //设置CardView的点击事件才会生效
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TestActivity","item.setOnClickListener(new View.OnClickListener()");
                    Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("OrderItem",item);
                    args.putString("Type",item.isOreder);//已预订  与 被预定两种状态
                    //好像..不需要其它信息了...
                    intent.putExtras(args);
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    public void setOrderList(List<PersonalOrderItem> list){
        this.orderList = list;
    }

    public interface OrderAction{
        public void dealAction(int way,PersonalOrderItem item);
    }
}
