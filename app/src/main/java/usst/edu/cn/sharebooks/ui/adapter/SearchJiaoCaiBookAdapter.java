package usst.edu.cn.sharebooks.ui.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;


import java.util.ArrayList;
import java.util.List;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.model.search.JiaoCai;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.ApiInterface;
import usst.edu.cn.sharebooks.ui.activity.SearchBookDetailActivity;


public class SearchJiaoCaiBookAdapter extends RecyclerView.Adapter {
    private List<JiaoCai> list = new ArrayList<>();
    private Context context;
    private User mUser;
    private BaseActivity mBaseActivity;
    private int ADD_TYPE;

    public SearchJiaoCaiBookAdapter(User user, BaseActivity baseActivity) {
        mUser = user;//将用户的数据传递到这里来
        mBaseActivity = baseActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SearchJiaoCaiResultViewHolder(LayoutInflater.from(context).inflate(R.layout.searchbookitem_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SearchJiaoCaiResultViewHolder) holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    public void setList(List<JiaoCai> list){
        this.list = list;
    }

    public void setAddType(int type){
        ADD_TYPE = type;
    }

    class SearchJiaoCaiResultViewHolder extends RecyclerView.ViewHolder{
        private ImageView mBookImage;
        private TextView mBookName;
        private TextView mAuthor;
        private TextView mPressVerison;
        private TextView mBrifeContent;

        public SearchJiaoCaiResultViewHolder(View itemView) {
            super(itemView);
            mBookImage = (ImageView) itemView.findViewById(R.id.iv_bookimage);
            mBookName = (TextView)itemView.findViewById(R.id.tv_bookName);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            mPressVerison = (TextView)itemView.findViewById(R.id.tv_pressversion);
            mBrifeContent = (TextView)itemView.findViewById(R.id.tv_bookIntro);
        }

        private void bind(final JiaoCai jiaoCai){
            String versionPress = jiaoCai.press+"/"+jiaoCai.version;
            String imageUrl = ApiInterface.AllBookImageUrl+jiaoCai.imageUrl;
          // Glide.with(context).load(imageUrl).into(mBookImage);
            GlideApp.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .override(200,200)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            mBookImage.setImageBitmap(resource);
                            Log.i("TestNormal","resource.getRowBytes()"+resource.getRowBytes());
                            Log.i("TestNormal","bitmap.getByteCount() kb"+resource.getByteCount()/1024);
                        }
                    });
            mBookName.setText(jiaoCai.bookName);
            mAuthor.setText(jiaoCai.author);
            mPressVerison.setText(versionPress);
            mBrifeContent.setText(jiaoCai.bookIntro);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //先不要管  书籍的类型
                    //暂且先忽略这个
                    Intent intent = new Intent(mBaseActivity,SearchBookDetailActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("User",mUser);
                    args.putInt("BookType",1);//这个是用于区别教材与其他书的添加
                    args.putSerializable("BookInfo",jiaoCai);
                    args.putInt("AddType",ADD_TYPE);//对进行添加的类型进行筛选  可能是卖书书摊 可能是贡献书籍....
                    //将图片放在args里传到 SearchBookDetailActivity
                    Bitmap bitmap = null;
                    BitmapDrawable bitmapDrawable =((BitmapDrawable) mBookImage.getDrawable());
                    if (bitmapDrawable != null){
                        bitmap =  bitmapDrawable.getBitmap();
                    }
                    args.putParcelable("book_image",bitmap);
                  //  args.putString("ImageUrl", ApiInterface.AllBookImageUrl+jiaoCai.imageUrl);
                    Log.i("TestNormal","bitmap.getRowBytes()"+bitmap.getRowBytes());
                    Log.i("TestNormal","bitmap.getByteCount() kb"+bitmap.getByteCount()/1024);
                    intent.putExtras(args);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(mBaseActivity,mBookImage,"book_image");
                        mBaseActivity.startActivity(intent,optionsCompat.toBundle());
                    }else {
                        mBaseActivity.startActivity(intent);
                    }
                }
            });
        }
    }
}
